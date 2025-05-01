import service from '@/utils/request'

// baseURL: "127.0.0.1:19090/system",
export function getQuestionList(params){
    return service({
        url: "/question/list",
        method: "get",
        params,
    });
}

export function addQuestion(params = {}){
    return service({
        url: "/question/add",
        method: "post",
        data: params,
    });
}

export function getQuestionDetail(questionId){
    return service({
        url: "/question/detail",
        method: "get",
        params: { questionId }
    });
}

export function editQuestionDetail(params = {}){
    return service({
        url: "/question/edit",
        method: "put",
        data: params,
    });
}

export function delQuestionDetail(questionId){
    return service({
        url: "/question/delete",
        method: "put",
        params: { questionId }
    });
}

export function getDeletedQuestionList(params){
    return service({
        url: "/question/deleted",
        method: "get",
        params,
    });
}
export function deleteQuestionForever(questionId){
    return service({
        url: "/question/remove",
        method: "put",
        params: { questionId }
    });
}

export function recoverQuestionInBin(questionId){
    return service({
        url: "/question/recover",
        method: "put",
        params: { questionId }
    });
}


