"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.UserServiceImpl = void 0;
const usermodel_1 = __importDefault(require("../../models/usermodel"));
const bcrypt_1 = __importDefault(require("bcrypt"));
const sequelize_1 = require("../database/sequelize");
// Service implementation for user operations (no strict interface enforcement)
class UserServiceImpl {
    async createUser(data, options) {
        if (data.password) {
            const hash = await bcrypt_1.default.hash(data.password, 10);
            data.password = hash;
        }
        // Ensure we only insert columns that exist in the database to avoid SQL errors
        // Use describeTable to avoid mariadb driver formatting issues
        const colsDesc = await sequelize_1.sequelize.getQueryInterface().describeTable('users');
        const existing = new Set(Object.keys(colsDesc));
        const insertData = {};
        if (existing.has('email') && (data.email || data.correo))
            insertData.email = data.email || data.correo;
        if (existing.has('password') && data.password)
            insertData.password = data.password;
        if (existing.has('usuario') && data.usuario)
            insertData.usuario = data.usuario;
        if (existing.has('nombre') && data.nombre)
            insertData.nombre = data.nombre;
        if (existing.has('apellido') && data.apellido)
            insertData.apellido = data.apellido;
        const user = (await usermodel_1.default.create(insertData, options));
        return user;
    }
    async getUserById(id) {
        return usermodel_1.default.findByPk(id);
    }
    async getUserByEmail(email) {
        return usermodel_1.default.findOne({ where: { email } });
    }
    // Find by username (case-sensitive lookup)
    async getUserByUsername(usuario) {
        return usermodel_1.default.findOne({ where: { usuario } });
    }
    async updateUser(id, data) {
        if (data.password) {
            const hash = await bcrypt_1.default.hash(data.password, 10);
            data.password = hash;
        }
        const [affected] = await usermodel_1.default.update(data, { where: { id_user: id } });
        return affected > 0;
    }
    async changePassword(id, currentPassword, newPassword) {
        const user = await usermodel_1.default.findByPk(id);
        if (!user)
            return false;
        const ok = await bcrypt_1.default.compare(currentPassword, user.password);
        if (!ok)
            return false;
        const hash = await bcrypt_1.default.hash(newPassword, 10);
        const [affected] = await usermodel_1.default.update({ password: hash }, { where: { id_user: id } });
        return affected > 0;
    }
    async deleteUser(id) {
        const affected = await usermodel_1.default.destroy({ where: { id_user: id } });
        return affected > 0;
    }
}
exports.UserServiceImpl = UserServiceImpl;
exports.default = new UserServiceImpl();
//# sourceMappingURL=UserServiceImpl.js.map