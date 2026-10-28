"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProfileService = void 0;
const profilemodel_1 = __importDefault(require("../../models/profilemodel"));
// Service for profile CRUD operations
class ProfileService {
    async createProfile(userId, data, options) {
        return profilemodel_1.default.create({ ...data, userId }, options);
    }
    async getProfileByUserId(userId) {
        return profilemodel_1.default.findOne({ where: { userId } });
    }
    async updateProfile(userId, data) {
        const [affected] = await profilemodel_1.default.update(data, { where: { userId } });
        return affected > 0;
    }
}
exports.ProfileService = ProfileService;
exports.default = new ProfileService();
//# sourceMappingURL=ProfileService.js.map