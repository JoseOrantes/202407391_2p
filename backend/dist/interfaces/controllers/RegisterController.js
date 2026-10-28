"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.register = void 0;
const sequelize_1 = require("../../infrastructure/database/sequelize");
const UserServiceImpl_1 = __importDefault(require("../../infrastructure/services/UserServiceImpl"));
const ProfileService_1 = __importDefault(require("../../infrastructure/services/ProfileService"));
// Register controller: creates user and profile in a transaction
// Returns standardResponse envelope with created resources
const register = async (req, res) => {
    const { nombre, apellido, usuario, password, fotoBase64, telefono, correo, email, fechaNac, genero } = req.body;
    const emailToUse = correo || email;
    if (!usuario || !password || !emailToUse)
        return res.status(400).json({ standardResponse: { httpCode: 400, message: 'usuario, email and password required' } });
    // Transaction: create user and profile together
    const t = await sequelize_1.sequelize.transaction();
    try {
        // create user (email stored in users table)
        const user = await UserServiceImpl_1.default.createUser({ nombre, apellido, usuario, password, email: emailToUse }, { transaction: t });
        // create profile (do not duplicate email in profile)
        const profile = await ProfileService_1.default.createProfile(user.id_user, { fotoBase64, telefono, fechaNac, genero }, { transaction: t });
        await t.commit();
        const userObj = (user.toJSON ? user.toJSON() : user);
        delete userObj.password;
        return res.status(201).json({
            standardResponse: { httpCode: 201, message: 'Usuario y perfil creados exitosamente' },
            body: { user: { id: userObj.id_user, nombre: userObj.nombre, apellido: userObj.apellido, usuario: userObj.usuario }, profile }
        });
    }
    catch (err) {
        await t.rollback();
        console.error('Register error:', err);
        return res.status(500).json({ standardResponse: { httpCode: 500, message: 'Registro falló' } });
    }
};
exports.register = register;
//# sourceMappingURL=RegisterController.js.map