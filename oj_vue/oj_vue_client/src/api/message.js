import service from "@/utils/request";

export function getMessageListService(params) {
  return service({
    url: "/client/message/list",
    method: "get",
    params,
  });
}