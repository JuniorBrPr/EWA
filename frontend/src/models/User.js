export class User{
    // constructor(id, name, email, password, role, team, tasksAssigned, ordersMade) {
    //     this.id = id;
    //     this.name = name;
    //     this.email = email;
    //     this.password = password;
    //     this.role = role;
    // }

    constructor(email, password) {
        this.email = email;
        this.password = password;
    }

    static roleList = [
        "Admin",
        "User",
    ];
}