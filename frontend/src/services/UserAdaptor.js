import {Adaptor} from "@/services/Adaptor";
import {User} from "@/models/User";

export default class UserAdaptor extends Adaptor {
    constructor(URL) {
        super(URL);
    }

    async asyncFindUser(user) {
        const options = {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(user)
        }

        const response = await this.fetchJson(this.resourceUrl + "/login", options);

        return response ? User.fromJson(response) : null;
    }

    async asyncFindAllByTeamId(teamId) {
        const options = {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }

        const response = await this.fetchJson(this.resourceUrl + "/team/" + teamId, options);

        return Array.isArray(response) ? response.map(user => User.fromJson(user)) : null;
    }

    async asyncFindAllByNoTeam() {
        const options = {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }

        const response = await this.fetchJson(this.resourceUrl + "/noTeam", options);

        return Array.isArray(response) ? response.map(user => User.fromJson(user)) : null;
    }

    async asyncDeleteById(id) {
        const options = {
            method: "DELETE", headers: {"Content-Type": "application/json"},
        }
        const response = await this.fetchJson(this.resourceUrl + "/" + id, options);

        return response ? User.fromJson(response) : null;
    }

    async asyncFindAll() {
        const options = {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }

        const response = await this.fetchJson(this.resourceUrl, options);

        return response ? response.map(user => User.fromJson(user)) : null;
    }

    async asyncGetById(id) {
        const options = {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        }

        const response = await this.fetchJson(this.resourceUrl + "/" + id, options);

        return response ? User.fromJson(response) : null;
    }

    async asyncSave(user) {
        console.log(user);
        const options = {
            method: "POST", headers: {"Content-Type": "application/json"}, body: JSON.stringify(user)
        }
        const response = await this.fetchJson(this.resourceUrl, options);
        return response ? User.fromJson(response) : null;
    }

    async asyncUpdate(id, user) {
        const options = {
            method: "PUT", headers: {"Content-Type": "application/json"}, body: JSON.stringify(user)
        }

        let response = await this.fetchJson(this.resourceUrl + "/" + id, options);

        return response ? User.fromJson(response) : null;
    }

    async asyncResetPassword(password, id) {
        const options = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({"password": password, "id": id}),
        };

        const response = await this.fetchJson(this.resourceUrl + "/passReset", options);

        return response ? User.fromJson(response) : null;
    }

    async asyncGetUserInfo(id) {
        const options = {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        };

        const response = await this.fetchJson(this.resourceUrl + id, options);
        return response ? User.fromJson(response) : null;
    }
}