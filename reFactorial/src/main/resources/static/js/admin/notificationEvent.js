const postItemList = document.querySelectorAll(".postItem")
const addPost = document.querySelector("#addPost")
const menuMainBox = document.querySelector(".menuMainBox")
const sideMenuList = document.querySelectorAll(".sideMenu")

addPost.addEventListener("click",()=>{
    sideMenuList.forEach(menu =>{
        if (menu.classList.contains("selected")){
            menu.classList.remove("selected")
        }
    })

    addPost.classList.add("selected")

    menuMainBox.innerHTML = '';
    menuMainBox.innerHTML = `
                                    <div class="postInfo">
                                        <div>
                                            <label for="postTitle">제목 : </label>
                                            <input id="postTitle" name="postTitle" type="text">
                                        </div>

                                        <div class="buttons">
                                            <button id="addPostBtn">저장하기</button>
                                        </div>
                                    </div>

                                    <div class="postInfo">
                                        <label for="postCategory">분류 : </label>
                                        <select id="postCategory">
                                            <option name="postCategory" value="1">공지사항</option>
                                            <option name="postCategory" value="5">이벤트</option>
                                        </select>
                                    </div>

                                    <div class="postInfo">
                                        <label for="postContent">내용 </label>
                                        <textarea id="postContent" name="postContent"></textarea>
                                    </div>
            `

    const addPostBtn = document.getElementById("addPostBtn")
    addPostBtn.addEventListener("click",savePost)
})

postItemList.forEach(postItem =>{
    postItem.addEventListener("click",(event)=>{
        const postId = event.currentTarget.dataset.postId;

        fetch(`/admin/postDetail?postId=${postId}`,{
            method : 'GET'
        }).then(response => response.json())
            .then(data =>{
                console.log("detail data : ",data)

                if (data.postDetail != null){
                    menuMainBox.innerHTML ='';

                    menuMainBox.innerHTML = `
                                    <div class="postInfo">
                                        <div>
                                            <label for="postTitle">제목 : </label>
                                            <input id="postTitle" name="postTitle" type="text" value="${data.postDetail.postTitle}" disabled>
                                        </div>

                                        <div class="buttons">
                                            <button id="modifyPost" data-post-id="${data.postDetail.postId}">수정하기</button>
                                            <button id="modifyDelete" data-post-id="${data.postDetail.postId}">삭제하기</button>
                                        </div>
                                    </div>

                                    <div class="postInfo">
                                        <label for="postDate">작성일 : </label>
                                        <input id="postDate" name="postDate" type="text" value="${data.postDetail.postCreationDate}" disabled>
                                    </div>

                                    <div class="postInfo">
                                        <label for="postContent">내용 </label>
                                        <textarea id="postContent" name="postContent" disabled>${data.postDetail.postContent}"</textarea>
                                    </div>
                            `

                    const modifyBtn = document.getElementById("modifyPost").addEventListener("click",modifyPost);
                    const modifyDelete = document.getElementById("modifyDelete").addEventListener("click",deletePost);
                }
            })
    })
})

function modifyPost(event){
    const inputTags = document.querySelectorAll("#postTitle, #postContent")
    const modifyBtn = event.currentTarget

    inputTags.forEach(tag => tag.disabled = false)

    modifyBtn.textContent= "저장하기"

    const modifyDelete = document.getElementById("modifyDelete")
    modifyDelete.style.display = "none"

    modifyBtn.removeEventListener("click",modifyPost);
    modifyBtn.addEventListener("click",savePost);
}

function savePost (event){
    const postId = event.currentTarget.dataset.postId

    fetch("/admin/modifyPost",{
        method:'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body:JSON.stringify({
            "postId": postId,
            "postTitle": document.getElementById("postTitle").value,
            "postContent": document.getElementById("postContent").value,
            "postCategory": document.getElementById("postCategory")?.value
        })
    })

    // 새 URL로 리디렉션
    location.reload(`/admin/postDetail?postId=${postId}`);
}

function deletePost (event){
    const postId = event.currentTarget.dataset.postId
    console.log("modifyDelete postId: ",postId)


    fetch(`/admin/deletePost?postId=${postId}   `,{
        method:"GET"
    })

    location.reload(`/admin/postDetail?postId=${postId}`);

}

document.addEventListener("DOMContentLoaded",()=>{
    const URL = new URLSearchParams(window.location.search)

    const categoryCode = URL.get("categoryCode")

    if (categoryCode == 1){
        sideMenuList.forEach(menu =>{
            if (menu.classList.contains("selected")){
                menu.classList.remove("selected")
            }
        })
        const notificationSelect = document.querySelector("#notificationSelect")
        notificationSelect.classList.add('selected')
    }

    if (categoryCode == 5){
        sideMenuList.forEach(menu =>{
            if (menu.classList.contains("selected")){
                menu.classList.remove("selected")
            }
        })
        const eventSelect = document.querySelector("#eventSelect")
        eventSelect.classList.add('selected')
    }

})
