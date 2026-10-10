#!/usr/bin/env python3
"""Reference builder for the E15 room-shell package (fixtures only).

Reads confirmed site measurements (#112 sync payload) from ``inputs/`` and
writes, per case, ``expected/<case>/`` with ``manifest.json``, ``walls.csv``,
``elements.csv`` and ``preview.svg``.

This is the executable form of ``../room-shell-contract.md``. Studio's Java
exporter must produce the same rows (same values after parsing) for the same
inputs; the Fusion add-in uses ``expected/`` as its test data.

Usage (stdlib only):
    python3 -I build_fixtures.py [inputs_dir] [expected_dir]
"""
import csv
import hashlib
import io
import json
import math
import sys
from pathlib import Path

SCHEMA_VERSION = 1
GENERATED_AT = "2026-10-09T00:00:00Z"  # fixed so fixtures are reproducible

DEFAULTS = {
    "wallThicknessMm": 150,
    "markerSizeMm": 80,
    "markerDepthMm": 25,
    "panelDepthMm": 10,
    "obstructionDepthMm": {"CL": 300, "VG": 300, "TB": 100},
    "closureToleranceMm": 20,
}

# code -> (group, labelEsMx, renderAs)
CATALOG = {
    "V": ("OPENING", "Ventana", "OPENING"),
    "P": ("OPENING", "Puerta", "OPENING"),
    "TA": ("SERVICE", "Toma de agua", "MARKER"),
    "DR": ("SERVICE", "Drenaje", "MARKER"),
    "GS": ("SERVICE", "Toma de gas", "MARKER"),
    "CT": ("SERVICE", "Contacto eléctrico", "MARKER"),
    "AP": ("SERVICE", "Apagador", "MARKER"),
    "CE": ("SERVICE", "Salida de extracción", "MARKER"),
    "CL": ("OBSTRUCTION", "Columna", "SOLID"),
    "VG": ("OBSTRUCTION", "Viga", "SOLID"),
    "TB": ("OBSTRUCTION", "Tubería", "SOLID"),
    "RG": ("OBSTRUCTION", "Registro", "PANEL"),
    "RF": ("APPLIANCE", "Refrigerador", "ENVELOPE"),
    "ES": ("APPLIANCE", "Estufa", "ENVELOPE"),
    "PA": ("APPLIANCE", "Parrilla", "ENVELOPE"),
    "HO": ("APPLIANCE", "Horno", "ENVELOPE"),
    "CA": ("APPLIANCE", "Campana", "ENVELOPE"),
    "MW": ("APPLIANCE", "Microondas", "ENVELOPE"),
    "LV": ("APPLIANCE", "Lavavajillas", "ENVELOPE"),
    "TJ": ("APPLIANCE", "Tarja (espacio)", "ENVELOPE"),
}

# code -> (colorHex, opacity)
APPEARANCE = {
    "WALL": ("#D9D9D9", 1.0),
    "V": ("#4FA3E0", 0.5),
    "P": ("#8D6E63", 0.5),
    "TA": ("#1E88E5", 1.0),
    "DR": ("#6D4C41", 1.0),
    "GS": ("#FBC02D", 1.0),
    "CT": ("#E53935", 1.0),
    "AP": ("#FB8C00", 1.0),
    "CE": ("#8E24AA", 1.0),
    "CL": ("#757575", 1.0),
    "VG": ("#757575", 1.0),
    "TB": ("#9E9E9E", 1.0),
    "RG": ("#BDBDBD", 1.0),
    "APPLIANCE": ("#26A69A", 0.35),
}

WALL_COLUMNS = [
    "wall_code", "seq", "length_mm", "length_floor_mm", "length_900_mm", "length_ceiling_mm",
    "height_left_mm", "height_right_mm", "thickness_mm", "out_of_plumb_mm",
    "direction_deg", "start_x_mm", "start_y_mm", "end_x_mm", "end_y_mm",
    "inner_normal_x", "inner_normal_y",
    "outer_start_x_mm", "outer_start_y_mm", "outer_end_x_mm", "outer_end_y_mm",
    "corner_start_code", "corner_start_angle_deg", "corner_end_code", "corner_end_angle_deg",
    "color_hex",
]

ELEMENT_COLUMNS = [
    "element_id", "element_uuid", "wall_code", "code", "group", "label_es_mx", "render_as",
    "x_mm", "y_mm", "width_mm", "height_mm", "depth_mm",
    "point_x_mm", "point_y_mm", "swing", "derived_fields",
    "global_x_mm", "global_y_mm", "global_z_mm",
    "color_hex", "opacity", "notes",
]


def val(field):
    return None if field is None else field.get("value")


def num(x, decimals=1):
    """Integers stay integers; others are rounded. Always '.' as decimal point."""
    if x is None:
        return ""
    r = round(float(x), decimals)
    if r == int(r):
        return str(int(r))
    return f"{r:.{decimals}f}".rstrip("0").rstrip(".")


def unit(deg):
    rad = math.radians(deg)
    return math.cos(rad), math.sin(rad)


def line_intersection(p, d, q, e):
    """Intersection of p + s*d and q + t*e; None if parallel."""
    den = d[0] * e[1] - d[1] * e[0]
    if abs(den) < 1e-9:
        return None
    s = ((q[0] - p[0]) * e[1] - (q[1] - p[1]) * e[0]) / den
    return p[0] + s * d[0], p[1] + s * d[1]


def build_walls(m):
    corners = {c["cornerCode"]: c for c in m["corners"]}
    walls = m["walls"]
    codes = [w["wallCode"] for w in walls]
    n = len(walls)
    closing_code = f"E-{codes[-1]}{codes[0]}"
    closed = closing_code in corners
    t = DEFAULTS["wallThicknessMm"]

    out = []
    pos, direction = (0.0, 0.0), 0.0
    for i, w in enumerate(walls):
        lengths = [val(w["lengthFloorMm"]), val(w["length900Mm"]), val(w["lengthCeilingMm"])]
        length = min(lengths)
        d = unit(direction)
        end = (pos[0] + d[0] * length, pos[1] + d[1] * length)
        start_corner = f"E-{codes[i - 1]}{codes[i]}" if (i > 0 or closed) else None
        end_corner = f"E-{codes[i]}{codes[(i + 1) % n]}" if (i < n - 1 or closed) else None
        out.append({
            "wall": w, "code": w["wallCode"], "seq": i + 1, "lengths": lengths, "length": length,
            "direction": direction % 360, "d": d, "normal": (d[1], -d[0]),
            "start": pos, "end": end,
            "start_corner": start_corner, "end_corner": end_corner,
            "start_angle": corners[start_corner]["angleDeg"] if start_corner else None,
            "end_angle": corners[end_corner]["angleDeg"] if end_corner else None,
        })
        if end_corner:
            direction -= 180 - corners[end_corner]["angleDeg"]  # clockwise turn
        pos = end

    # Outer face: offset by thickness away from the room, mitered at corners.
    for i, w in enumerate(out):
        off = (-w["normal"][0] * t, -w["normal"][1] * t)
        outer_line = ((w["start"][0] + off[0], w["start"][1] + off[1]), w["d"])

        def miter(other, at):
            o_off = (-other["normal"][0] * t, -other["normal"][1] * t)
            other_line = ((at[0] + o_off[0], at[1] + o_off[1]), other["d"])
            return line_intersection(outer_line[0], outer_line[1], other_line[0], other_line[1])

        prev_w = out[i - 1] if w["start_corner"] else None
        next_w = out[(i + 1) % n] if w["end_corner"] else None
        w["outer_start"] = (miter(prev_w, w["start"]) if prev_w else None) or (w["start"][0] + off[0], w["start"][1] + off[1])
        w["outer_end"] = (miter(next_w, w["end"]) if next_w else None) or (w["end"][0] + off[0], w["end"][1] + off[1])

    gap = None
    if closed:
        last = out[-1]["end"]
        gap = (last[0], last[1])
    angle_sum = sum(c["angleDeg"] for c in m["corners"])
    expected_sum = (n - 2) * 180 if closed else None
    return out, closed, gap, angle_sum, expected_sum


def ceiling_at(w, x):
    hl, hr = val(w["wall"]["ceilingHeightLeftMm"]), val(w["wall"]["ceilingHeightRightMm"])
    return hl + (hr - hl) * x / w["length"]


def resolve_element(e, w):
    code = e["code"]
    group, label, render_as = CATALOG[code]
    t = DEFAULTS["wallThicknessMm"]
    x, y = val(e["xMm"]), val(e["yMm"])
    wd, h, d = val(e["widthMm"]), val(e["heightMm"]), val(e["depthMm"])
    derived = []
    px = py = None

    if render_as == "OPENING":
        if code == "P":
            if y is None:
                derived.append("Y")
            y = 0
        d = t
    elif render_as == "MARKER":
        m = DEFAULTS["markerSizeMm"]
        px, py = x, y
        x, y, wd, h, d = x - m / 2, y - m / 2, m, m, DEFAULTS["markerDepthMm"]
    elif render_as == "PANEL":
        d = DEFAULTS["panelDepthMm"]
    elif render_as == "SOLID":
        if code == "CL":
            if y is None:
                y = 0
                derived.append("Y")
            if h is None:
                h = min(ceiling_at(w, x), ceiling_at(w, x + wd)) - y
                derived.append("H")
        elif code == "VG" and y is None:
            y = min(ceiling_at(w, x), ceiling_at(w, x + wd)) - h
            derived.append("Y")
        elif code == "TB":
            if y is None:
                y = 0
                derived.append("Y")
            if h is None:
                h = min(ceiling_at(w, x), ceiling_at(w, x + wd)) - y
                derived.append("H")
        if d is None:
            d = DEFAULTS["obstructionDepthMm"][code]
            derived.append("DEPTH")
    elif render_as == "ENVELOPE":
        if y is None:
            y = 0
            derived.append("Y")

    color, opacity = APPEARANCE.get(code) or APPEARANCE[group]
    gx = w["start"][0] + w["d"][0] * x
    gy = w["start"][1] + w["d"][1] * x
    return {
        "element_uuid": e["elementUuid"], "wall_code": w["code"], "code": code, "group": group,
        "label_es_mx": label, "render_as": render_as,
        "x_mm": num(x), "y_mm": num(y), "width_mm": num(wd), "height_mm": num(h), "depth_mm": num(d),
        "point_x_mm": num(px), "point_y_mm": num(py),
        "swing": e.get("swing") or "",
        "derived_fields": ";".join(derived),
        "global_x_mm": num(gx), "global_y_mm": num(gy), "global_z_mm": num(y),
        "color_hex": color, "opacity": num(opacity, 2), "notes": e.get("notes") or "",
        "_sort": (x if px is None else px, code),
        "_box": (x, y, wd, h, d),
    }


def to_csv(columns, rows):
    buf = io.StringIO()
    writer = csv.DictWriter(buf, fieldnames=columns, lineterminator="\r\n", extrasaction="ignore")
    writer.writeheader()
    writer.writerows(rows)
    return "﻿" + buf.getvalue()


def build_case(m, out_dir):
    walls, closed, gap, angle_sum, expected_sum = build_walls(m)
    t = DEFAULTS["wallThicknessMm"]
    warnings = []

    wall_rows = []
    for w in walls:
        ww = w["wall"]
        wall_rows.append({
            "wall_code": w["code"], "seq": w["seq"], "length_mm": num(w["length"]),
            "length_floor_mm": num(w["lengths"][0]), "length_900_mm": num(w["lengths"][1]),
            "length_ceiling_mm": num(w["lengths"][2]),
            "height_left_mm": num(val(ww["ceilingHeightLeftMm"])),
            "height_right_mm": num(val(ww["ceilingHeightRightMm"])),
            "thickness_mm": t, "out_of_plumb_mm": num(val(ww.get("outOfPlumbMm"))),
            "direction_deg": num(w["direction"], 3),
            "start_x_mm": num(w["start"][0]), "start_y_mm": num(w["start"][1]),
            "end_x_mm": num(w["end"][0]), "end_y_mm": num(w["end"][1]),
            "inner_normal_x": num(w["normal"][0], 6), "inner_normal_y": num(w["normal"][1], 6),
            "outer_start_x_mm": num(w["outer_start"][0]), "outer_start_y_mm": num(w["outer_start"][1]),
            "outer_end_x_mm": num(w["outer_end"][0]), "outer_end_y_mm": num(w["outer_end"][1]),
            "corner_start_code": w["start_corner"] or "", "corner_start_angle_deg": num(w["start_angle"]),
            "corner_end_code": w["end_corner"] or "", "corner_end_angle_deg": num(w["end_angle"]),
            "color_hex": APPEARANCE["WALL"][0],
        })

    element_rows = []
    for w in walls:
        resolved = sorted((resolve_element(e, w) for e in w["wall"]["elements"]), key=lambda r: r["_sort"])
        counters = {}
        for r in resolved:
            counters[r["code"]] = counters.get(r["code"], 0) + 1
            r["element_id"] = f"{w['code']}-{r['code']}-{counters[r['code']]:02d}"
            element_rows.append(r)

    closure_gap = math.hypot(*gap) if gap else None
    if closed and closure_gap > DEFAULTS["closureToleranceMm"]:
        warnings.append({"code": "ROOM_NOT_CLOSED",
                         "message": f"El contorno no cierra: {num(closure_gap)} mm entre el final del muro {walls[-1]['code']} y el origen"})
    if closed and abs(angle_sum - expected_sum) > 0.5:
        warnings.append({"code": "ROOM_ANGLE_SUM_MISMATCH",
                         "message": f"Suma de ángulos {num(angle_sum)}° vs {expected_sum}° esperados"})

    walls_csv = to_csv(WALL_COLUMNS, wall_rows)
    elements_csv = to_csv(ELEMENT_COLUMNS, element_rows)
    fx = m.get("_fixture", {})
    site = m.get("site") or {}
    manifest = {
        "contract": "kfs-room-shell",
        "schemaVersion": SCHEMA_VERSION,
        "units": "mm",
        "frame": {
            "up": "+Z",
            "origin": "left end of wall A, interior face, finished floor",
            "wallA": "+X",
            "roomSide": "right of the wall direction (walls run clockwise seen from above)",
        },
        "session": {"sessionCode": fx.get("sessionCode"), "projectType": m.get("projectType"), "spaceLabel": fx.get("spaceLabel")},
        "source": {"measurementUuid": m["measurementUuid"], "revision": m["revision"], "catalogVersion": m["catalogVersion"]},
        "generatedAt": GENERATED_AT,
        "room": {
            "closed": closed,
            "wallCount": len(walls),
            "closureGapMm": None if gap is None else float(num(closure_gap)),
            "closureGapXMm": None if gap is None else float(num(gap[0])),
            "closureGapYMm": None if gap is None else float(num(gap[1])),
            "angleSumDeg": angle_sum,
            "expectedAngleSumDeg": expected_sum,
            "maxCeilingHeightMm": max(max(val(w["wall"]["ceilingHeightLeftMm"]), val(w["wall"]["ceilingHeightRightMm"])) for w in walls),
            "floorOutOfLevelMm": site.get("floorOutOfLevelMm"),
            "floorOutOfLevelNote": site.get("floorOutOfLevelNote"),
        },
        "defaults": DEFAULTS,
        "files": {
            "walls": {"name": "walls.csv", "rows": len(wall_rows), "sha256": hashlib.sha256(walls_csv.encode("utf-8")).hexdigest()},
            "elements": {"name": "elements.csv", "rows": len(element_rows), "sha256": hashlib.sha256(elements_csv.encode("utf-8")).hexdigest()},
        },
        "warnings": warnings,
    }

    out_dir.mkdir(parents=True, exist_ok=True)
    (out_dir / "walls.csv").write_bytes(walls_csv.encode("utf-8"))
    (out_dir / "elements.csv").write_bytes(elements_csv.encode("utf-8"))
    (out_dir / "manifest.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    (out_dir / "preview.svg").write_text(preview_svg(manifest, walls, element_rows), encoding="utf-8")
    return manifest


# ---------------------------------------------------------------- preview SVG

def esc(s):
    return str(s).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace('"', "&quot;")


def preview_svg(manifest, walls, rows):
    plan_scale, elev_scale, margin = 0.12, 0.08, 40
    pts = [p for w in walls for p in (w["start"], w["end"], w["outer_start"], w["outer_end"])]
    for r in rows:  # doors swinging out extend beyond the walls
        if r["code"] == "P" and r["swing"].endswith("_OUT"):
            w = next(w for w in walls if w["code"] == r["wall_code"])
            x, _, wd, _, _ = r["_box"]
            for u in (x, x + wd):
                pts.append((w["start"][0] + w["d"][0] * u - w["normal"][0] * (DEFAULTS["wallThicknessMm"] + wd),
                            w["start"][1] + w["d"][1] * u - w["normal"][1] * (DEFAULTS["wallThicknessMm"] + wd)))
    min_x, max_x = min(p[0] for p in pts), max(p[0] for p in pts)
    min_y, max_y = min(p[1] for p in pts), max(p[1] for p in pts)
    plan_w = (max_x - min_x) * plan_scale + 2 * margin
    top = 60
    plan_h = (max_y - min_y) * plan_scale + 2 * margin + top

    def P(x, y):  # plan mm -> svg px (SVG y grows downward)
        return margin + (x - min_x) * plan_scale, top + margin + (max_y - y) * plan_scale

    def G(w, u, n=0.0):  # wall-local (u along wall, n into room) -> plan mm
        return (w["start"][0] + w["d"][0] * u + w["normal"][0] * n,
                w["start"][1] + w["d"][1] * u + w["normal"][1] * n)

    svg = []
    title = f"{manifest['session']['sessionCode']} — {manifest['session']['spaceLabel']} (rev {manifest['source']['revision']})"
    svg.append(f'<text x="{margin}" y="24" font-size="16" font-weight="bold">{esc(title)}</text>')
    if manifest["warnings"]:
        svg.append(f'<text x="{margin}" y="44" font-size="11" fill="#C62828">{esc(" · ".join(w["code"] for w in manifest["warnings"]))}</text>')

    for w in walls:
        poly = [w["start"], w["end"], w["outer_end"], w["outer_start"]]
        svg.append('<polygon points="%s" fill="#D9D9D9" stroke="#424242" stroke-width="1"/>'
                   % " ".join("%.1f,%.1f" % P(*p) for p in poly))
        lx, ly = P(*G(w, w["length"] / 2, -DEFAULTS["wallThicknessMm"] - 180))
        svg.append(f'<text x="{lx:.1f}" y="{ly:.1f}" font-size="14" font-weight="bold" text-anchor="middle" dominant-baseline="middle">{w["code"]}</text>')
    ox, oy = P(0, 0)
    svg.append(f'<circle cx="{ox:.1f}" cy="{oy:.1f}" r="4" fill="#C62828"/><text x="{ox + 6:.1f}" y="{oy - 6:.1f}" font-size="10" fill="#C62828">0,0</text>')

    by_wall = {w["code"]: w for w in walls}
    for r in rows:
        w = by_wall[r["wall_code"]]
        x, y, wd, h, d = r["_box"]
        if r["render_as"] == "OPENING":
            a, b = P(*G(w, x, -DEFAULTS["wallThicknessMm"] / 2)), P(*G(w, x + wd, -DEFAULTS["wallThicknessMm"] / 2))
            svg.append(f'<line x1="{a[0]:.1f}" y1="{a[1]:.1f}" x2="{b[0]:.1f}" y2="{b[1]:.1f}" stroke="{r["color_hex"]}" stroke-width="5"/>')
            if r["code"] == "P" and r["swing"] not in ("", "NONE", "SLIDING"):
                hinge_u = x if r["swing"].startswith("LEFT") else x + wd
                free_u = x + wd if r["swing"].startswith("LEFT") else x
                side = 1 if r["swing"].endswith("_IN") else -1
                n0 = 0 if side == 1 else -DEFAULTS["wallThicknessMm"]
                hp, fp = G(w, hinge_u, n0), G(w, free_u, n0)
                op = G(w, hinge_u, n0 + side * wd)
                hp_, fp_, op_ = P(*hp), P(*fp), P(*op)
                rad = wd * plan_scale
                # sweep direction depends on orientation in screen space
                cross = (fp_[0] - hp_[0]) * (op_[1] - hp_[1]) - (fp_[1] - hp_[1]) * (op_[0] - hp_[0])
                sweep = 1 if cross > 0 else 0
                svg.append(f'<path d="M{op_[0]:.1f},{op_[1]:.1f} L{hp_[0]:.1f},{hp_[1]:.1f} M{fp_[0]:.1f},{fp_[1]:.1f} A{rad:.1f},{rad:.1f} 0 0 {1 - sweep} {op_[0]:.1f},{op_[1]:.1f}" fill="none" stroke="{r["color_hex"]}" stroke-dasharray="4 3"/>')
        else:
            poly = [G(w, x, 0), G(w, x + wd, 0), G(w, x + wd, d), G(w, x, d)]
            svg.append('<polygon points="%s" fill="%s" fill-opacity="%s" stroke="#212121" stroke-width="0.6"/>'
                       % (" ".join("%.1f,%.1f" % P(*p) for p in poly), r["color_hex"], max(float(r["opacity"]), 0.35)))

    # Elevations (seen from inside the room, wall left end on the left)
    y0 = plan_h + 20
    x0 = margin
    row_h = 0
    width = max(plan_w, 900)
    for w in walls:
        ww = w["wall"]
        hl, hr = val(ww["ceilingHeightLeftMm"]), val(ww["ceilingHeightRightMm"])
        ew, eh = w["length"] * elev_scale, max(hl, hr) * elev_scale
        if x0 + ew + margin > width:
            x0, y0, row_h = margin, y0 + row_h + 50, 0
        base = y0 + 20 + eh
        E = lambda u, v: (x0 + u * elev_scale, base - v * elev_scale)
        poly = [E(0, 0), E(w["length"], 0), E(w["length"], hr), E(0, hl)]
        svg.append(f'<text x="{x0:.1f}" y="{y0 + 12:.1f}" font-size="12" font-weight="bold">Muro {w["code"]} — {num(w["length"])} mm</text>')
        svg.append('<polygon points="%s" fill="#F5F5F5" stroke="#424242"/>' % " ".join("%.1f,%.1f" % p for p in poly))
        for r in rows:
            if r["wall_code"] != w["code"]:
                continue
            x, y, wd, h, _ = r["_box"]
            a = E(x, y + h)
            svg.append(f'<rect x="{a[0]:.1f}" y="{a[1]:.1f}" width="{wd * elev_scale:.1f}" height="{h * elev_scale:.1f}" fill="{r["color_hex"]}" fill-opacity="{max(float(r["opacity"]), 0.35)}" stroke="#212121" stroke-width="0.5"/>')
            if r["render_as"] == "MARKER":  # label above small markers
                c = E(x + wd / 2, y + h)
                svg.append(f'<text x="{c[0]:.1f}" y="{c[1] - 3:.1f}" font-size="8" text-anchor="middle">{esc(r["code"])}</text>')
            else:
                c = E(x + wd / 2, y + h / 2)
                svg.append(f'<text x="{c[0]:.1f}" y="{c[1]:.1f}" font-size="8" text-anchor="middle" dominant-baseline="middle">{esc(r["code"])}</text>')
        x0 += ew + margin
        row_h = max(row_h, eh + 30)

    total_h = y0 + row_h + 40
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="{width:.0f}" height="{total_h:.0f}" '
            f'font-family="sans-serif"><rect width="100%" height="100%" fill="white"/>\n'
            + "\n".join(svg) + "\n</svg>\n")


def main():
    here = Path(__file__).resolve().parent
    inputs = Path(sys.argv[1]) if len(sys.argv) > 1 else here / "inputs"
    expected = Path(sys.argv[2]) if len(sys.argv) > 2 else here / "expected"
    for f in sorted(inputs.glob("*.json")):
        m = json.loads(f.read_text(encoding="utf-8"))
        manifest = build_case(m, expected / f.stem)
        warn = ", ".join(w["code"] for w in manifest["warnings"]) or "none"
        print(f"{f.stem}: walls={manifest['files']['walls']['rows']} elements={manifest['files']['elements']['rows']} "
              f"closed={manifest['room']['closed']} gap={manifest['room']['closureGapMm']} warnings={warn}")


if __name__ == "__main__":
    main()
