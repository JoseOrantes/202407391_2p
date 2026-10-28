"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const dotenv_1 = __importDefault(require("dotenv"));
const userRoutes_1 = __importDefault(require("./interfaces/routes/userRoutes"));
const authRoutes_1 = __importDefault(require("./interfaces/routes/authRoutes"));
dotenv_1.default.config();
const corsOrigins = (process.env.CORS_ORIGINS || '')
    .split(',')
    .map((origin) => origin.trim())
    .filter(Boolean);
const app = (0, express_1.default)();
app.use((0, cors_1.default)({
    origin(origin, callback) {
        if (!origin || corsOrigins.length === 0 || corsOrigins.includes(origin)) {
            return callback(null, true);
        }
        return callback(new Error('CORS origin not allowed'));
    },
}));
app.use(express_1.default.json());
app.use('/api/users', userRoutes_1.default);
app.use('/api', authRoutes_1.default);
// Health check endpoint
app.get('/health', (_req, res) => {
    res.json({ status: 'ok' });
});
exports.default = app;
//# sourceMappingURL=app.js.map