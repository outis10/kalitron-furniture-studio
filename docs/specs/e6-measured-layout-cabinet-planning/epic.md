# E6 Measured Layout and Cabinet List Generation

Status: Draft
Issue: #38

## Problem

Reference photos and AI summaries help describe the desired kitchen or closet,
but they cannot be the source of truth for fabrication dimensions. The system
needs a measured layout contract before it can generate a reliable cabinet list.

## Goal

Capture a measured room layout, mark zones and obstacles, generate a cabinet
plan with deterministic rules, and let the designer review cabinet dimensions
before BOM, quote, or Fusion 360 generation.

## Child Issues

- #39 Capture a measured room layout.
- #40 Mark appliances and obstacles on the layout.
- #41 Define layout and cabinet planning contracts.
- #44 Generate a cabinet list from the measured layout.
- #42 Review and edit cabinet dimensions before fabrication.
- #43 Add deterministic cabinet layout validation rules.

## Principle

AI Gateway may suggest intent from chat or images, but Studio owns measured
dimensions, validation, cabinet placement, and persistence.
