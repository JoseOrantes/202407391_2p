import { Model, Optional } from 'sequelize';
interface ProfileAttributes {
    id: number;
    userId: number;
    fotoBase64?: string | null;
    telefono?: string | null;
    fechaNac?: Date | null;
    genero?: string | null;
}
interface ProfileCreationAttributes extends Optional<ProfileAttributes, 'id'> {
}
export declare class Profile extends Model<ProfileAttributes, ProfileCreationAttributes> implements ProfileAttributes {
    id: number;
    userId: number;
    fotoBase64: string | null;
    telefono: string | null;
    correo: string | null;
    fechaNac: Date | null;
    genero: string | null;
}
export default Profile;
//# sourceMappingURL=profilemodel.d.ts.map