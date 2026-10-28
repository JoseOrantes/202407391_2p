"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.User = void 0;
const sequelize_1 = require("sequelize");
const sequelize_2 = require("../infrastructure/database/sequelize");
class User extends sequelize_1.Model {
}
exports.User = User;
User.init({
    id_user: { type: sequelize_1.DataTypes.INTEGER.UNSIGNED, autoIncrement: true, primaryKey: true },
    email: { type: sequelize_1.DataTypes.STRING(200), allowNull: false, unique: true },
    nombre: { type: sequelize_1.DataTypes.STRING(100), allowNull: true },
    apellido: { type: sequelize_1.DataTypes.STRING(100), allowNull: true },
    usuario: { type: sequelize_1.DataTypes.STRING(100), allowNull: true, unique: true },
    password: { type: sequelize_1.DataTypes.STRING(255), allowNull: false },
}, { sequelize: sequelize_2.sequelize, tableName: 'users', timestamps: true });
exports.default = User;
//# sourceMappingURL=usermodel.js.map