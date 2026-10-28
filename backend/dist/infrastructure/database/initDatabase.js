"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.initDatabase = initDatabase;
const sequelize_1 = require("./sequelize");
async function initDatabase() {
    await sequelize_1.sequelize.authenticate();
    await sequelize_1.sequelize.sync();
    console.log('Database connected and synced.');
}
exports.default = initDatabase;
//# sourceMappingURL=initDatabase.js.map