"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.updateProfile = exports.createProfile = exports.getProfile = void 0;
const ProfileService_1 = __importDefault(require("../../infrastructure/services/ProfileService"));
// Controller for profile endpoints
// Returns profile data in standardResponse envelope
const getProfile = async (req, res) => {
    const userId = Number(req.params.id);
    const profile = await ProfileService_1.default.getProfileByUserId(userId);
    if (!profile)
        return res.status(404).json({ standardResponse: { httpCode: 404, message: 'Perfil no encontrado' } });
    return res.json({ standardResponse: { httpCode: 200, message: 'Perfil encontrado' }, body: profile });
};
exports.getProfile = getProfile;
const createProfile = async (req, res) => {
    const userId = Number(req.params.id);
    const data = req.body;
    const profile = await ProfileService_1.default.createProfile(userId, data);
    return res.status(201).json({ standardResponse: { httpCode: 201, message: 'Perfil creado exitosamente' }, body: profile });
};
exports.createProfile = createProfile;
const updateProfile = async (req, res) => {
    const userId = Number(req.params.id);
    const data = req.body;
    const ok = await ProfileService_1.default.updateProfile(userId, data);
    if (!ok)
        return res.status(404).json({ standardResponse: { httpCode: 404, message: 'Perfil no encontrado' } });
    const profile = await ProfileService_1.default.getProfileByUserId(userId);
    return res.json({ standardResponse: { httpCode: 200, message: 'Perfil actualizado exitosamente' }, body: profile });
};
exports.updateProfile = updateProfile;
exports.default = {};
//# sourceMappingURL=ProfileController.js.map