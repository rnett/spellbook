config.entry = {
    main: [require('path').resolve(__dirname, "kotlin/load.mjs")]
};
config.experiments = {...config.experiments, topLevelAwait: true};
config.resolve ?? (config.resolve = {});
config.resolve.alias ?? (config.resolve.alias = {});
config.resolve.alias.skia = false;