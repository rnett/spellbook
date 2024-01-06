import {instantiate} from './spellbook.uninstantiated.mjs';

await wasmSetup;

instantiate({skia: Module['asm']});