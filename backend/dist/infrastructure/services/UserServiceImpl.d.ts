import User from '../../models/usermodel';
export declare class UserServiceImpl {
    createUser(data: Partial<User>, options?: any): Promise<User>;
    getUserById(id: number): Promise<User | null>;
    getUserByEmail(email: string): Promise<User | null>;
    getUserByUsername(usuario: string): Promise<User | null>;
    updateUser(id: number, data: Partial<User>): Promise<boolean>;
    changePassword(id: number, currentPassword: string, newPassword: string): Promise<boolean>;
    deleteUser(id: number): Promise<boolean>;
}
declare const _default: UserServiceImpl;
export default _default;
//# sourceMappingURL=UserServiceImpl.d.ts.map