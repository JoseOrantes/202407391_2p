import { Model, Optional } from 'sequelize';
interface UserAttributes {
    id_user: number;
    email: string;
    password: string;
    nombre?: string;
    apellido?: string;
    usuario?: string;
}
interface UserCreationAttributes extends Optional<UserAttributes, 'id_user'> {
}
export declare class User extends Model<UserAttributes, UserCreationAttributes> implements UserAttributes {
    id_user: number;
    email: string;
    password: string;
    nombre: string;
    apellido: string;
    usuario: string;
}
export default User;
//# sourceMappingURL=usermodel.d.ts.map