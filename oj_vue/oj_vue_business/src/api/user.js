import service from '@/utils/request'

export function getUser(params) {
  return service({
    url: "/user/list",
    method: "get",
    params,
  });
}

export function updateStatus(params = {}) {
  return service({
    url: "/user/updateStatus",
    method: "put",
    data: params,
  });
}