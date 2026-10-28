"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.Profile = void 0;
const sequelize_1 = require("sequelize");
const sequelize_2 = require("../infrastructure/database/sequelize");
const usermodel_1 = __importDefault(require("./usermodel"));
// Profile model
class Profile extends sequelize_1.Model {
}
exports.Profile = Profile;
Profile.init({
    id: { type: sequelize_1.DataTypes.INTEGER.UNSIGNED, autoIncrement: true, primaryKey: true },
    userId: { type: sequelize_1.DataTypes.INTEGER.UNSIGNED, allowNull: false },
    fotoBase64: { type: sequelize_1.DataTypes.TEXT, allowNull: true },
    telefono: { type: sequelize_1.DataTypes.STRING(50), allowNull: true },
    fechaNac: { type: sequelize_1.DataTypes.DATE, allowNull: true },
    genero: { type: sequelize_1.DataTypes.STRING(10), allowNull: true },
}, { sequelize: sequelize_2.sequelize, tableName: 'profiles', timestamps: true });
// Association: Profile belongs to User
Profile.belongsTo(usermodel_1.default, { foreignKey: 'userId', as: 'user' });
usermodel_1.default.hasOne(Profile, { foreignKey: 'userId', as: 'profile' });
exports.default = Profile;
//# sourceMappingURL=profilemodel.js.map