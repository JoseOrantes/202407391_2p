"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = auth;
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
// JWT secret for verifying tokens
const SECRET = process.env.JWT_SECRET || 'change-this-secret';
function auth(req, res, next) {
    const h = req.header('authorization') || req.header('Authorization');
    if (!h)
        return res.status(401).json({ error: 'Authorization header required' });
    const parts = h.split(' ');
    if (parts.length !== 2 || parts[0] !== 'Bearer')
        return res.status(401).json({ error: 'Invalid authorization header' });
    const token = parts[1];
    if (!token)
        return res.status(401).json({ error: 'Invalid authorization header' });
    try {
        const payload = jsonwebtoken_1.default.verify(token, SECRET);
        req.userId = payload.id;
        next();
    }
    catch (err) {
        return res.status(401).json({ error: 'Invalid token' });
    }
}
//# sourceMappingURL=auth.js.map