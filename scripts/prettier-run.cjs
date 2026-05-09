#!/usr/bin/env node
'use strict';
const { spawnSync } = require('child_process');
const path = require('path');

const prettierBin = path.resolve(__dirname, '..', 'node_modules', 'prettier', 'bin', 'prettier.cjs');
const result = spawnSync(process.execPath, ['--stack-size=65536', prettierBin, ...process.argv.slice(2)], {
  stdio: 'inherit',
});
process.exit(result.status ?? 0);
