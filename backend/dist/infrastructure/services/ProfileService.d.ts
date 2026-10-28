import Profile from '../../models/profilemodel';
export declare class ProfileService {
    createProfile(userId: number, data: Partial<Profile>, options?: any): Promise<void | Profile>;
    getProfileByUserId(userId: number): Promise<Profile | null>;
    updateProfile(userId: number, data: Partial<Profile>): Promise<boolean>;
}
declare const _default: ProfileService;
export default _default;
//# sourceMappingURL=ProfileService.d.ts.map