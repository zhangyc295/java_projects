import service from "@/utils/request";

export function sendCodeService(params = {}) {
  return service({
    url: "/client/sendCode",
    method: "post",
    data: params,
  });
}

export function codeLoginService(params = {}) {
  return service({
    url: "/client/code/login",
    method: "post",
    data: params,
  });
}

export function accountLoginService(params = {}) {
  return service({
    url: "/client/account/login",
    method: "post",
    data: params,
  });
}

export function logoutService() {
  return service({
    url: "/client/logout",
    method: "delete",
  });
}

export function getUserInfoService() {
  return service({
    url: "/client/info",
    method: "get",
  });
}

export function getUserDetailService(contestId) {
  return service({
    url: "/client/detail",
    method: "get",
    params: { contestId },
  });
}

export function editUserService(params = {}) {
  return service({
    url: "/client/edit",
    method: "put",
    data: params,
  });
}

export function editUserPasswordService(params = {}) {
  return service({
    url: "/client/password",
    method: "put",
    data: params,
  });
}

export function updateHeadImageService(params = {}) {
  return service({
    url: "/client/headImage/update",
    method: "put",
    data: params,
  });
}

export function userSubmitService(params = {}) {
  return service({
    url: "/client/question/rabbitmq/submit",
    method: "post",
    data: params,
  });
}

