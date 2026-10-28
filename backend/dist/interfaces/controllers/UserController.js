"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.updatePassword = exports.deleteUser = exports.updateUser = exports.getUserByUsername = exports.getUserByEmail = exports.getUserById = exports.createUser = void 0;
const UserServiceImpl_1 = __importDefault(require("../../infrastructure/services/UserServiceImpl"));
const createUser = async (req, res) => {
    const { email, password } = req.body;
    if (!email || !password)
        return res.status(400).json({ error: 'email and password are required' });
    const user = await UserServiceImpl_1.default.createUser({ email, password });
    const obj = user.toJSON ? user.toJSON() : user;
    delete obj.password;
    res.status(201).json(obj);
};
exports.createUser = createUser;
const getUserById = async (req, res) => {
    const id = Number(req.params.id);
    const user = await UserServiceImpl_1.default.getUserById(id);
    if (!user)
        return res.status(404).json({ error: 'Not found' });
    const obj = user.toJSON ? user.toJSON() : user;
    delete obj.password;
    res.json(obj);
};
exports.getUserById = getUserById;
const getUserByEmail = async (req, res) => {
    const raw = req.params.email;
    const email = Array.isArray(raw) ? raw[0] : raw;
    if (!email)
        return res.status(400).json({ error: 'email required' });
    const user = await UserServiceImpl_1.default.getUserByEmail(email);
    if (!user)
        return res.status(404).json({ error: 'Not found' });
    const obj = user.toJSON ? user.toJSON() : user;
    delete obj.password;
    res.json(obj);
};
exports.getUserByEmail = getUserByEmail;
// Retrieve user by username
const getUserByUsername = async (req, res) => {
    const usuario = req.params.usuario;
    if (!usuario)
        return res.status(400).json({ error: 'usuario required' });
    const user = await UserServiceImpl_1.default.getUserByUsername(usuario);
    if (!user)
        return res.status(404).json({ error: 'Not found' });
    const obj = user.toJSON ? user.toJSON() : user;
    delete obj.password;
    res.json(obj);
};
exports.getUserByUsername = getUserByUsername;
const updateUser = async (req, res) => {
    const id = Number(req.params.id);
    const ok = await UserServiceImpl_1.default.updateUser(id, req.body);
    res.json({ success: ok });
};
exports.updateUser = updateUser;
const deleteUser = async (req, res) => {
    const id = Number(req.params.id);
    const ok = await UserServiceImpl_1.default.deleteUser(id);
    res.json({ success: ok });
};
exports.deleteUser = deleteUser;
const updatePassword = async (req, res) => {
    const id = Number(req.params.id);
    const { currentPassword, newPassword } = req.body;
    if (!currentPassword || !newPassword)
        return res.status(400).json({ error: 'currentPassword and newPassword required' });
    const ok = await UserServiceImpl_1.default.changePassword(id, currentPassword, newPassword);
    if (!ok)
        return res.status(400).json({ error: 'current password incorrect or update failed' });
    res.json({ success: true });
};
exports.updatePassword = updatePassword;
//# sourceMappingURL=UserController.js.map