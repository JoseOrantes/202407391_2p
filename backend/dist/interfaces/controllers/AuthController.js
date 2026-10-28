"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.login = void 0;
const UserServiceImpl_1 = __importDefault(require("../../infrastructure/services/UserServiceImpl"));
const bcrypt_1 = __importDefault(require("bcrypt"));
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
// Secret used to sign JWTs
const SECRET = process.env.JWT_SECRET || 'change-this-secret';
const login = async (req, res) => {
    const { email, password, usuario } = req.body;
    const identifier = email || usuario;
    if (!identifier || !password)
        return res.status(400).json({ error: 'usuario/email and password required' });
    const user = await UserServiceImpl_1.default.getUserByEmail(identifier);
    if (!user)
        return res.status(401).json({ error: 'invalid credentials' });
    const ok = await bcrypt_1.default.compare(password, user.password);
    if (!ok)
        return res.status(401).json({ error: 'invalid credentials' });
    const token = jsonwebtoken_1.default.sign({ id: user.id_user }, SECRET, { expiresIn: '1h' });
    const userObj = user.toJSON ? user.toJSON() : user;
    delete userObj.password;
    return res.json({
        standardResponse: { httpCode: 200, message: 'Login exitoso' },
        body: { token, user: { id: userObj.id_user || userObj.id, email: userObj.email } }
    });
};
exports.login = login;
//# sourceMappingURL=AuthController.js.map