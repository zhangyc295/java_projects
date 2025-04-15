import service from '@/utils/request'

// baseURL: "127.0.0.1:19090/system",
export function adminLogin(account, password){
    return service({
        url: "/admin/login",
        method: "post",
        data: {account, password}
    })
}

export function getInfo(){
    return service({
        url: "/admin/info",
        method: "get"
    })
}

export function adminLogout(){
    return service({
        url: "/admin/logout",
        method: "delete"
    })
}