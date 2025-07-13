import service from "@/utils/request";

export function getQuestionListService(params) {
  return service({
    url: "/question/semiLogin/list",
    method: "get",
    params,
  });
}

export function getHotQuestionListService() {
  return service({
    url: "/question/semiLogin/hotList",
    method: "get"
  });
}

export function getQuestionDetailService(questionId) {
  return service({
    url: "/question/detail",
    method: "get",
    params: { questionId },
  });
}

export function preQuestionService(questionId) {
  return service({
    url: "/question/prev",
    method: "get",
    params: { questionId },
  });
}

export function nextQuestionService(questionId) {
  return service({
    url: "/question/next",
    method: "get",
    params: { questionId },
  });
}

export function getQuestionResultService(questionId, contestId, submitTime) {
  return service({
    url: "/client/question/result",
    method: "get",
    params: { questionId, contestId, submitTime }
  });
}