import service from '@/utils/request'

// baseURL: "127.0.0.1:19090/system",
export function getContestList(params){
    return service({
        url: "/contest/list",
        method: "get",
        params,
    });
}


export function contestAdd(params = {}) {
    return service({
        url: "/contest/add",
        method: "post",
        data: params,
    });
}


export function addContestQuestion(params = {}) {
    return service({
        url: "/contest/question/add",
        method: "post",
        data: params,
    });
}

export function getContestDetail(contestId) {
    return service({
        url: "/contest/detail",
        method: "get",
        params: { contestId },
    });
}

export function editContestQuestion(params = {}) {
    return service({
      url: "/contest/edit",
      method: "put",
      data: params,
    });
}

export function delContestQuestion(contestId, questionId) {
    return service({
      url: "/contest/question/delete",
      method: "delete",
      params: { contestId, questionId },
    });
}

export function delContest(contestId) {
    return service({
      url: "/contest/delete",
      method: "put",
      params: { contestId },
    });
}

export function publishContest(contestId) {
    return service({
      url: "/contest/publish",
      method: "put",
      params: { contestId },
    });
}
  
export function cancelPublishContest(contestId) {
    return service({
      url: "/contest/cancelPublish",
      method: "put",
      params: { contestId },
    });
}