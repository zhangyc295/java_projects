import service from "@/utils/request";

export function getContestListService(params) {
  return service({
    url: "/contest/semiLogin/redis/list",
    method: "get",
    params,
  });
}


export function enterContestService(enterContestDTO) {
    return service({
      url: "/client/contest/enter",
      method: "post",
      data: enterContestDTO
    });
}

export function getContestRankListService(params) {
  return service({
    url: "/contest/rank/list",
    method: "get",
    params,
  });
}

// export function getMyContestListService(params = {}) {
//   return service({
//     url: "/client/contest/list",
//     method: "get",
//     params,
//   });
// }

export function getContestFirstQuestionService(contestId) {
  return service({
    url: "/contest/getFirstQuestion",
    method: "get",
    params: { contestId },
  });
}

export function contestPrevQuestionService(contestId, questionId) {
  return service({
    url: "/contest/prev",
    method: "get",
    params: { contestId,questionId},
  });
}

export function contestNextQuestionService(contestId, questionId) {
  return service({
    url: "/contest/next",
    method: "get",
    params: { contestId, questionId },
  });
}