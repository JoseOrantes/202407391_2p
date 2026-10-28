"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const app_1 = __importDefault(require("./app"));
const initDatabase_1 = __importDefault(require("./infrastructure/database/initDatabase"));
const dotenv_1 = __importDefault(require("dotenv"));
dotenv_1.default.config();
const PORT = process.env.PORT || 3000;
const HOST = process.env.HOST || '0.0.0.0';
async function start() {
    await (0, initDatabase_1.default)();
    app_1.default.listen(Number(PORT), HOST, () => {
        console.log(`Server listening on ${HOST}:${PORT}`);
    });
}
void start();
//# sourceMappingURL=main.js.map