module.exports = {
  '{,.blueprint/**/,src/**/,webpack/}*.{md,json,yml,js,cjs,mjs,ts,cts,mts,java,html,tsx,css,scss}': [
    'node scripts/prettier-run.cjs --write',
  ],
};
