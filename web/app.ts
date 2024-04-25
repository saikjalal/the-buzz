// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or


// methods, without the compiler producing an error.
var $: any;

// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var newEntryForm: NewEntryForm;
var newCommentForm: NewCommentForm;
var editUserForm: EditUserForm;

//This is only the local server. 
// This constant indicates the path to our back-end server (change to your own)
const backendUrl = "https://2023sp-softserve.dokku.cse.lehigh.edu";
//const backendUrl = "http://localhost:4567";


var sessionID = 'testToken';
var cookieID: string; //session id for the user
var cookieUser: string; // user of the logged in user
var myUser: User; // just a user class to hold their info
// var files:string; // file 

//Handles the google sign in button and recieves all the information like the userid and google information.
// Will send a post request to backend for authenication.
function handleCredentialResponse(response: any) {
    var profile = decodeJwtResponse(response.credential);
    console.log('ID: ' + profile.sub);
    console.log('Name: ' + profile.name);
    console.log('Image URL: ' + profile.picture);
    console.log('Email: ' + profile.email);

    const doAjax = async () => {
        await fetch(`${backendUrl}/session_authenticate`, {
            method: 'POST',
            body: JSON.stringify({
                oauth_token: response.credential
            }),
            headers: {
                'Content-type': 'application/json; charset=UTF-8'
            }
        }).then((response) => {
            // If we get an "ok" message, return the json
            console.log(response);
            if (response.ok) {
                return Promise.resolve(response.json());
            }
            // Otherwise, handle server errors with a detailed popup message
            else {
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            return Promise.reject(response);
        }).then((data) => {
            sessionID = data.mData.sessionToken;
            let dataUserID = data.mData.userID;
            console.log('user id' + sessionID);
            document.cookie = 'userID=' + dataUserID;
            document.cookie = 'sessionID=' + sessionID;
            console.log(document.cookie);
            if (sessionID != null) {
                //made a change here. 
                //window.location.href = "https://2023sp-softserve.dokku.cse.lehigh.edu";
                window.location.href = "http://localhost:8080";
                //window.location.href = "http://localhost:4567";
            }
            // console.log("Returned ID " + sessionID);
        }).catch((error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error");
        });
    }
    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}

function decodeJwtResponse(data: any) {
    var tokens = data.split(".");
    return JSON.parse(atob(tokens[1]));
}

//Cookie function to save the session id needed for Authorization
function getCookie(dataName: string) {
    let id = dataName + '=';
    let decodedCookie = decodeURIComponent(document.cookie);
    let cookies = decodedCookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        let c = cookies[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(id) == 0) {
            console.log(" cookie info " + c.substring(id.length, c.length));
            return c.substring(id.length, c.length);
        }
    }
    return "";
}

cookieID = getCookie("sessionID");
cookieUser = getCookie("userID");

//////////////////////// Start of New Entry Form ///////////////////////////

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
class NewEntryForm {
    /**
     * To initialize the object, we say what method of NewEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */

    //base64 encoding method that is only to be used in newentryform 
    private encodeImageToBase64(file: File): Promise<string> {
        // create a file reader here
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => {
                // create image object to load the image 
                const image = new Image();
                image.onload = () => {
                    //gonna use a canvas element that will draw the element.
                    const canvas = document.createElement("canvas");
                    canvas.width = image.width;
                    canvas.height = image.height;
                    //render a 2d image if possible
                    const context = canvas.getContext("2d");
                    //if there is a 2d image, then continue
                    if (context) {
                        context.drawImage(image, 0, 0);
                        const base64Data = canvas.toDataURL(file.type); // here, you will convert the canvas data to a base64 string
                        resolve(base64Data);
                    } else {
                        //throw this / reject promise if there is an issue loading the image
                        reject(new Error("Failed to get 2D context"));
                    }
                };
                image.onerror = () => {
                    reject(new Error("Failed to load image"));
                };
                image.src = reader.result as string;
            };
            reader.onerror = () => {
                // reject the promise if there is an error reading the file or if there 
                reject(new Error("Failed to read file"));
            };
            reader.readAsDataURL(file); // this should read the file as a dataURL
        });
    }

    constructor() {
        document.getElementById("addCancel")?.addEventListener("click", (e) => { newEntryForm.clearForm(); });
        document.getElementById("addButton")?.addEventListener("click", (e) => { newEntryForm.submitForm(); });
        document.getElementById("addFile")?.addEventListener("click", (e) => { newEntryForm.submitForm(); }); // Add a file button
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("newMessage")).value = "";
        // reset the UI
        // REMOVE (<HTMLElement>document.getElementById("editElement")).style.display = "none";
        (<HTMLElement>document.getElementById("addElement")).style.display = "none";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let msg = "" + (<HTMLInputElement>document.getElementById("newMessage")).value;
        if (msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }

        //adding a link to messages and the url can be found using regex. 

        // Extract link from the message using regex
        const linkRegex = /(http|ftp|https):\/\/([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:\/~+#-]*[\w@?^=%&\/~+#-])/; // uses this regex to find url 
        const linkMatch = msg.match(linkRegex);
        let link = linkMatch ? linkMatch[0] : null; // linkmatch is called if regex search works 
        let linkData = new FormData();
        linkData.append("mMessage", msg);
        if (link) {
            linkData.append("mLink", link);
        }

        //link ends here, file starts here. 

        let fileInput = document.getElementById("fileInput") as HTMLInputElement;
        if (fileInput != null) {
            let file = fileInput.files?.[0]; // chaining operator used to fix 'object is possibly null error'
            if (file != null) {
                this.encodeImageToBase64(file); // call base64 encoder method made in newentry form
                linkData.append("mFile", file); // that should work, and user should be able to post a file
                // for the encoding here, I was having trouble resolving the value of the promise, so a file (any image) couldn't successfully be serialized. 
            }
        }

        /*
        // get the selected file, if any
        let fileInput = (<HTMLInputElement>document.getElementById("fileInput"));
        let file = fileInput.files[0];
        if (file) {
            formData.append("mFile", file);
        }
        */

        //this link data will be sent back to the backend server using the fetch method (HTTP request that has already been used)

        // is null if there is no match

        //submitting a file in a message? ? ? \

        // A lot of commented out stuff, just doing it for error handling at the moment. 
        /*
        const fileInput = document.getElementById("fileInput") as HTMLInputElement;
        if (fileInput != null) {
            const file = fileInput.files[0];
            if (file != null) {
                const reader = new FileReader();
                reader.readAsDataURL(file); // 
                reader.onload = function(event)
                const linkData = new FormData();
                const msg = "This is a message with an attached file.";
                linkData.append("mMessage", msg);
                //linkData.append("mFile", file);
                // make your AJAX request here with linkData as the payload
            } else {
                window.alert("Error: No file selected.");
            }
        } 
        */



        // alot of commented out stuff
        /*
        let fileInput = (<HTMLInputElement>document.getElementById("fileInput")); //document.getElementById("fileInput") as HTMLInputElement;
        if(fileInput != null){
        //let file = fileInput.files[0]; // possibly null object still ? getting error stating that it is still null even after checking that it isn't null
        } 
        
        else {
            window.alert("Error: File is empty");
            return;
        }
        
        let formData = new FormData(); 
        formData.append("mMessage", msg);
        */


        //formData.append("mFile", file);

        //some lines commented out because of null error. Should be fine. 

        // AJAX POST //

        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        /**
         * Use fetch to make an http POST to the backend
         * @return resolved promise of JSON response (not quite sure what this means)
         */
        const doAjax = async () => {
            await fetch(`${backendUrl}/messages`, {
                method: 'POST',
                body: JSON.stringify({
                    mMessage: msg
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                newEntryForm.onSubmitResponse(data);
                mainList.refresh();
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            newEntryForm.clearForm();
            mainList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }

} // end class NewEntryForm


//////////////// Start of New Comment Form ///////////////////////
class NewCommentForm {
    /**
     * To initialize the object, we say what method of NewCommentForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("addCommentCancel")?.addEventListener("click", (e) => { newCommentForm.clearCommentForm(); });
    }
    /**
     * Clear the comment form's input fields
     */
    clearCommentForm() {
        (<HTMLInputElement>document.getElementById("newComment")).value = "";
        // reset the UI
        // REMOVE (<HTMLElement>document.getElementById("editElement")).style.display = "none";
        (<HTMLElement>document.getElementById("addComment")).style.display = "none";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    }
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitCommentForm(msgID: number) {

        // for links and messages, getting the values and then checking if their empty or not


        // document.getElementById("addCommentButton")?.removeEventListener("click", newCommentForm.submitCommentForm);
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let msg = "" + (<HTMLInputElement>document.getElementById("newComment")).value; // new message
        let linkField = <HTMLInputElement>document.getElementById("commentLink");
        let link = "" + linkField.value;
        let imgField = <HTMLInputElement>document.getElementById("commentImg");
        let img = "" + imgField.value; // get the type of image. 

        //Extract URLS via regex
        const urlRegex = /(http|ftp|https):\/\/([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:\/~+#-]*[\w@?^=%&\/~+#-])/;
        const urls = msg.match(urlRegex);

        //doesn't work I believe 
        //provide image tags
        //will be searchable via regex
        /*
        let commentText = msg.replace(urlRegex, '<a href="$1" target="_blank">$1</a>');
        if (link) {
            commentText += ` <a href="${link}" target="_blank">${link}</a>`;
        } if (img) {
            commentText += ` <img src="${img}" alt="Image">`;
        }
        */

        //This doesn't work I believe

        // check if the comment isn't empty
        if (msg === "") {
            window.alert("Error: comment is not valid");
            return;
        }

        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponseComment
        /**
         * Use fetch to make an http POST to the backend
         * @return resolved promise of JSON response (not quite sure what this means)
         */
        const doAjax = async () => {
            await fetch(`${backendUrl}/comment`, {
                method: 'POST',
                body: JSON.stringify({
                    messageId: msgID,
                    comment: msg
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                newCommentForm.onSubmitResponseComment(data, msgID);
                // mainList.refresh();
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitEditCommentForm(msgID: number, commentID: number) {
        // document.getElementById("addCommentButton")?.removeEventListener("click", newCommentForm.submitCommentForm);
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let msg = "" + (<HTMLInputElement>document.getElementById("newComment")).value;
        let link = "" + (<HTMLInputElement>document.getElementById("commentLink")).value; // for links in comments
        // I don't think the option of adding files to comments are necessary at all, but this would have to do. 
        let fileInput = <HTMLInputElement>document.getElementById("commentFile"); // Get the file input element
        let file = fileInput.files?.[0]; // Get the selected file
        // validating links using URL
        const urlRegex = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-.,@?^=%&:\/~+#]*[\w\-@?^=%&\/~+#])?/;
        if (link !== "" && !urlRegex.test(link)) {
            window.alert("Error: Invalid link");
            return;
        }
        if (msg === "") {
            window.alert("Error: comment is not valid");
            return;
        }

        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponseComment
        /**
         * Use fetch to make an http POST to the backend
         * @return resolved promise of JSON response (not quite sure what this means)
         */
        const doAjax = async () => {
            await fetch(`${backendUrl}/comment/${commentID}`, {
                method: 'PUT',
                body: JSON.stringify({
                    messageId: msgID,
                    comment: msg
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                newCommentForm.onSubmitResponseComment(data, msgID);
                // mainList.refreshCmnt(msgID);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    /**
    * onSubmitResponseComment runs when the AJAX call in submitCommentForm() returns a 
    * result.
    * @param data The object returned by the server
    */
    private onSubmitResponseComment(data: any, msgID: number) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            newCommentForm.clearCommentForm();
            mainList.refreshCmnt(msgID);
            // mainList.refreshCmnt(msgID);
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
}// END new Comment Form



///////////////////////////// Element List Start ////////////////////////////

// a global for the main ElementList of the program.  See newEntryForm for 
// explanation
var mainList: ElementList;

/**
 * ElementList provides a way of seeing all of the data stored on the server.
 */
class ElementList {
    /**
     * refresh is the public method for updating messageList
     */
    refresh() {
        // Issue an AJAX GET and then pass the result to update(). 
        console.log("messages Session ID " + cookieID + ' userid ' + cookieUser);
        const doAjax = async () => {
            await fetch(`${backendUrl}/messages`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.update(data);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * refreshCmnt is the public method for updating messageList with comments
     */
    refreshCmnt(msgID: number) {
        // Issue an AJAX GET and then pass the result to update(). 
        // console.log("messages Session ID " + cookieID);
        const doAjax = async () => {
            await fetch(`${backendUrl}/messages`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                // If we get an "ok" message, clear the form
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.updateCmnt(data, msgID);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * Update iterates through the list of messages, displays each message and its like count, and creates the buttons for liking, disliking, and deleting esach message
     * @param data messages data passed from GET request to backend
     */
    private update(data: any) {
        console.log(data);
        let elem_messageList = document.getElementById("messageList");

        if (elem_messageList !== null) {
            elem_messageList.innerHTML = "";

            let fragment = document.createDocumentFragment();
            let table = document.createElement('table');
            let tr = document.createElement('tr');
            let th_user = document.createElement('th');
            let th_msgTitle = document.createElement('th');
            th_user.innerHTML = "User #";
            th_msgTitle.innerHTML = "Message";
            th_msgTitle.setAttribute('colspan', "6");
            tr.appendChild(th_user);
            tr.appendChild(th_msgTitle);
            table.appendChild(tr);

            for (let i = 0; i < data.mData.length; i++) {//let i = data.mData.length -1 ; i >= 0; --i
                let tr = document.createElement('tr');
                tr.setAttribute('msg-id', data.mData[i].mId);
                let td_owner = document.createElement('td');
                td_owner.classList.add('viewProfile');
                let td_message = document.createElement('td');
                //let td_id = document.createElement('td');
                let td_likes = document.createElement('td');
                // TODO: should this be mContent, when I'm supposed to put mMessage above?
                td_owner.innerHTML = data.mData[i].userID;
                td_message.innerHTML = data.mData[i].mContent;
                //td_id.innerHTML = data.mData[i].mId;
                td_likes.innerHTML = "Likes: " + data.mData[i].mLikes
                //tr.appendChild(td_id);
                tr.appendChild(td_owner);
                tr.appendChild(td_message);
                tr.appendChild(td_likes);
                tr.appendChild(this.buttons(data.mData[i].mId, false));
                table.appendChild(tr);
            }
            fragment.appendChild(table);

            elem_messageList.appendChild(fragment);
        }

        // Find all of the delete buttons, and set their behavior
        /**
         * Iterates through each delete button and adds an event listener, calling clickDelete on that message if pressed
         *
        const all_delbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("delbtn"));
        for (let i = 0; i < all_delbtns.length; ++i) {
            all_delbtns[i].addEventListener("click", (e) => {mainList.clickDelete( e );});
        }
        */


        /**
         * Iterates through each like button and adds an event listener, calling clickLike on that message if pressed
         */
        const all_likebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
        for (let i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].addEventListener("click", (e) => { mainList.clickLike(e); });
        }

        /**
         * Iterates through each dislike button and adds an event listener, calling clickDislike on that message if pressed
         */
        const all_dlbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("dlbtn"));
        for (let i = 0; i < all_dlbtns.length; ++i) {
            all_dlbtns[i].addEventListener("click", (e) => { mainList.clickDislike(e); });
        }

        /**
         * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
         */
        const all_showCommentbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("cmntbtn"));
        for (let i = 0; i < all_showCommentbtns.length; ++i) {
            all_showCommentbtns[i].addEventListener("click", (e) => { mainList.clickShow(e); });
        }

        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        */
        const all_addCommentbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("addcmntbtn"));
        for (let i = 0; i < all_addCommentbtns.length; ++i) {
            all_addCommentbtns[i].addEventListener("click", (e) => { mainList.clickAddCmnt(e); });
        }

        /**
        * Iterates through each of the table data blocks that show the user id so that you can click them and see the profiles
        */
        const all_userIdBlocks = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("viewProfile"));
        for (let i = 0; i < all_userIdBlocks.length; ++i) {
            all_userIdBlocks[i].addEventListener("click", (e) => { mainList.getUserID(e) });
        }
    }

    /**
     * UpdateCmnt will do the same as Update above but will instead include the comments for the choosen message
     * @param data messages data passed from GET request to backend
     * @param msgID message id of the message you want to show the comments for
     */
    private updateCmnt(data: any, msgID: number) {
        console.log(data);
        let elem_messageList = document.getElementById("messageList");

        if (elem_messageList !== null) {
            elem_messageList.innerHTML = "";

            let fragment = document.createDocumentFragment();
            let table = document.createElement('table');

            for (let i = 0; i < data.mData.length; i++) {//let i = data.mData.length -1 ; i >= 0; --i
                let rowID = data.mData[i].mId;
                let tr = document.createElement('tr');
                tr.setAttribute('msg-id', data.mData[i].mId);
                let td_owner = document.createElement('td');
                td_owner.classList.add('viewProfile');
                let td_message = document.createElement('td');
                //let td_id = document.createElement('td');
                let td_likes = document.createElement('td');
                // TODO: should this be mContent, when I'm supposed to put mMessage above?
                td_owner.innerHTML = data.mData[i].userID;
                td_message.innerHTML = data.mData[i].mContent;
                //td_id.innerHTML = data.mData[i].mId;
                td_likes.innerHTML = "Likes: " + data.mData[i].mLikes
                //tr.appendChild(td_id);
                tr.appendChild(td_owner);
                tr.appendChild(td_message);
                tr.appendChild(td_likes);
                if (rowID == msgID) {
                    tr.appendChild(this.buttons(data.mData[i].mId, true));
                } else {
                    tr.appendChild(this.buttons(data.mData[i].mId, false));
                }
                table.appendChild(tr);
                if (rowID == msgID) { // If the messageID is equal to the message it is currently creating then it should also add/create its comments
                    for (let j = data.mData[i].mComments.length - 1; j >= 0; --j) {//let j = 0; j < data.mData[i].mComments.length; j++)
                        let tr = document.createElement('tr');
                        tr.setAttribute('msg-id', data.mData[i].mId);
                        tr.setAttribute('comment-id', data.mData[i].mComments[j].commentID);
                        let td_owner = document.createElement('td');
                        td_owner.classList.add('viewProfile');
                        let td_message = document.createElement('td');
                        td_owner.innerHTML = data.mData[i].mComments[j].userID;
                        td_message.innerHTML = data.mData[i].mComments[j].comment;
                        tr.appendChild(td_owner);
                        tr.appendChild(td_message);
                        tr.appendChild(this.cmntButtons(data.mData[i].mId, data.mData[i].mComments[j].commentID));
                        table.appendChild(tr);
                    }
                }
            }
            fragment.appendChild(table);

            elem_messageList.appendChild(fragment);
        }

        // Find all of the delete buttons, and set their behavior
        /**
         * Iterates through each delete button and adds an event listener, calling clickDelete on that message if pressed
         *
        const all_delbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("delbtn"));
        for (let i = 0; i < all_delbtns.length; ++i) {
            all_delbtns[i].addEventListener("click", (e) => {mainList.clickDelete( e );});
        }
        */

        /**
         * Iterates through each like button and adds an event listener, calling clickLike on that message if pressed
         */
        const all_likebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
        for (let i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].addEventListener("click", (e) => { mainList.clickLike(e); });
        }

        /**
         * Iterates through each dislike button and adds an event listener, calling clickDislike on that message if pressed
         */
        const all_dlbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("dlbtn"));
        for (let i = 0; i < all_dlbtns.length; ++i) {
            all_dlbtns[i].addEventListener("click", (e) => { mainList.clickDislike(e); });
        }

        /**
         * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
         */
        const all_showCommentbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("cmntbtn"));
        for (let i = 0; i < all_showCommentbtns.length; ++i) {
            all_showCommentbtns[i].addEventListener("click", (e) => { mainList.clickShow(e); });
        }

        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        */
        const all_addCommentbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("addcmntbtn"));
        for (let i = 0; i < all_addCommentbtns.length; ++i) {
            all_addCommentbtns[i].addEventListener("click", (e) => { mainList.clickAddCmnt(e); });
        }
        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        */
        const all_editCommentbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("editcmntbtn"));
        for (let i = 0; i < all_editCommentbtns.length; ++i) {
            all_editCommentbtns[i].addEventListener("click", (e) => { mainList.clickEditCmnt(e); });
        }

        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        *
        const all_deleteCommentbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("delcmntbtn"));
        for (let i = 0; i < all_deleteCommentbtns.length; ++i) {
            all_deleteCommentbtns[i].addEventListener("click", (e) => {mainList.clickDelCmnt( e );});
        }
       */

        /**
        * Iterates through each of the table data blocks that show the user id so that you can click them and see the profiles
        */
        const all_userIdBlocks = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("viewProfile"));
        for (let i = 0; i < all_userIdBlocks.length; ++i) {
            all_userIdBlocks[i].addEventListener("click", (e) => { mainList.getUserID(e) });
        }
    }

    /**
     * buttons() adds a 'delete', 'like', and 'dislike' button to the HTML for each row
     * @param id of the message you are currently setting up the buttons for
     * @param showingCmnt check to see if the message is showing its comments. Depening on this the show comments button will display Show more or Show Less
     * @return DocumentFragment containing delete, like, and dislike buttons
     */
    private buttons(id: string, showingCmnt: boolean): DocumentFragment {
        let fragment = document.createDocumentFragment();
        let td = document.createElement('td');

        // create edit button, add to new td, add td to returned fragment; REMOVED
        let btn = document.createElement('button');

        // create delete button, add to new td, add td to returned fragment//////////// uncomment to enable delete message
        /*
         td = document.createElement('td');
         btn = document.createElement('button');
         btn.classList.add("delbtn");
         btn.setAttribute('data-value', id);
         btn.innerHTML = 'Delete';
         td.appendChild(btn);
         fragment.appendChild(td);
         */

        // create like button
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("likebtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'Like';
        td.appendChild(btn);
        fragment.appendChild(td);

        // create dislike button
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("dlbtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'Dislike';
        td.appendChild(btn);
        fragment.appendChild(td);

        if (showingCmnt == true) {
            // create show less comments button for when comments are already shown
            td = document.createElement('td');
            btn = document.createElement('button');
            btn.classList.add("cmntbtn");
            btn.setAttribute('data-value', id);
            btn.innerHTML = 'Show Less';
            td.appendChild(btn);
            fragment.appendChild(td);
        } else {
            // create show more comments button
            td = document.createElement('td');
            btn = document.createElement('button');
            btn.classList.add("cmntbtn");
            btn.setAttribute('data-value', id);
            btn.innerHTML = 'Show More';
            td.appendChild(btn);
            fragment.appendChild(td);
        }

        // create add comment button
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("addcmntbtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'Comment!';
        td.appendChild(btn);
        fragment.appendChild(td);

        //create a button for adding files
        // create add file button
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("addfilebtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'Add File';
        td.appendChild(btn);
        fragment.appendChild(td);

        //allows users to add files in comments? I do not know if this is allowed? 

        return fragment;
    }
    /**
      * cmntButtons() adds a 'delete' and 'Edit' button to each comment row that is being displayed
      * @param id of the comment you are currently setting up the buttons for
      * @return DocumentFragment containing delete, like, and dislike buttons
      */
    private cmntButtons(msgID: string, cmntID: string): DocumentFragment {
        let fragment = document.createDocumentFragment();
        let td = document.createElement('td');

        // create edit button, add to new td, add td to returned fragment; REMOVED
        let btn = document.createElement('button');

        // create delete button, add to new td, add td to returned fragment
        /*
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("delcmntbtn");
        btn.setAttribute('msg-value', msgID);
        btn.setAttribute('cmnt-value', cmntID);
        btn.innerHTML = 'Delete';
        td.appendChild(btn);
        fragment.appendChild(td);
        */

        // crete an add file button

        // create add comment button
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("editcmntbtn");
        btn.setAttribute('msg-value', msgID);
        btn.setAttribute('cmnt-value', cmntID);
        btn.innerHTML = 'Edit';
        td.appendChild(btn);
        fragment.appendChild(td);

        return fragment;
    }

    /**
     * clickDelCmnt is the response to a click of a Delete comment button 
     */
    private clickDelCmnt(e: Event) {
        let btn = (<HTMLElement>e.target);
        let messageID = Number(btn.getAttribute("msg-value"));
        let commentID = Number(btn.getAttribute("cmnt-value"));
        // Issue an AJAX DELETE and then invoke refresh()
        const doAjax = async () => {
            await fetch(`${backendUrl}/comment/${commentID}`, {
                method: 'DELETE',
                body: JSON.stringify({
                    messageId: messageID,
                    comment_id: commentID
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.refreshCmnt(messageID);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    /**
     * clickEditCmnt is the response to a click of a Edit comment button 
     */
    private clickEditCmnt(e: Event) {
        let btn = (<HTMLElement>e.target);
        let messageID = Number(btn.getAttribute("msg-value"));
        let commentID = Number(btn.getAttribute("cmnt-value"));
        (<HTMLElement>document.getElementById("addComment")).style.display = "block";
        (<HTMLElement>document.getElementById("showElements")).style.display = "none";
        document.getElementById("addCommentButton")?.addEventListener("click", (e) => { newCommentForm.submitEditCommentForm(messageID, commentID); }, { once: true });
    }

    /**
     * clickAddCmnt is the response to a click of a Show Comment button, will open up the comment form that will then send a request
     */
    private clickAddCmnt(e: Event) {
        let btn = (<HTMLElement>e.target);
        let messageRowID = Number(btn.getAttribute("data-value"));
        (<HTMLElement>document.getElementById("addComment")).style.display = "block";
        (<HTMLElement>document.getElementById("showElements")).style.display = "none";
        document.getElementById("addCommentButton")?.addEventListener("click", (e) => { newCommentForm.submitCommentForm(messageRowID); }, { once: true });
    }

    /**
     * clickShow is the response to a click of a Show Less/More button, will make the message comment appear or go away
     */
    private clickShow(e: Event) {
        let btn = (<HTMLElement>e.target);
        if (btn.innerHTML == 'Show Less') {
            mainList.refresh();
        } else {
            let messageRowID = Number(btn.getAttribute("data-value"));
            mainList.refreshCmnt(messageRowID);
        }
    }

    /**
     * clickDelete is the response to a click of a delete button, and send an HTTP DELETE to the message of the given ID
     */
    private clickDelete(e: Event) {
        const id = (<HTMLElement>e.target).getAttribute("data-value");

        // Issue an AJAX DELETE and then invoke refresh()
        const doAjax = async () => {
            await fetch(`${backendUrl}/messages/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.refresh();
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * clickLike is the response to a click of a delete button, and sends an HTTP PUT to like the message of the given ID
     */
    private clickLike(e: Event) {
        // get the id of the row
        const id = (<HTMLElement>e.target).getAttribute("data-value");

        // Issue an AJAX PUT and then invoke refresh()
        const doAjax = async () => {
            await fetch(`${backendUrl}/messages/l/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.refresh();
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * clickDislike is the response to a click of a delete button, and sends an HTTP PUT to like the message of the given ID
     */
    private clickDislike(e: Event) {
        // get the id of the row
        const id = (<HTMLElement>e.target).getAttribute("data-value");

        // Issue an AJAX PUT and then invoke refresh()
        const doAjax = async () => {
            await fetch(`${backendUrl}/messages/d/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.refresh();
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    private getUserID(e: Event) {
        const tableBlock = (<HTMLElement>e.target);
        const userNum = tableBlock.innerHTML;
        console.log("Messages User id is: " + userNum);
        showOtherProfile(Number(userNum));
        (<HTMLElement>document.getElementById("otherProfile")).style.display = "block";
        (<HTMLElement>document.getElementById("showElements")).style.display = "none";

    }
} ///////// end class ElementList /////////////////////////////

//for phase 3, don't need to write any code or alter code in profile section
/////////// Start of Profile Functions ////////////////
function showMyProfile() {
    const doAjax = async () => {
        await fetch(`${backendUrl}/profile/${cookieUser}`, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
                'Authorization': cookieID
            }
        }).then((response) => {
            if (response.ok) {
                return Promise.resolve(response.json());
            }
            else {
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            return Promise.reject(response);
        }).then((data) => {
            setMyProfileData(data);
            console.log(data);
        }).catch((error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error");
        });
    }
    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}
function showOtherProfile(userNum: number) {
    const doAjax = async () => {
        await fetch(`${backendUrl}/profile/${userNum}`, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
                'Authorization': cookieID
            }
        }).then((response) => {
            if (response.ok) {
                return Promise.resolve(response.json());
            }
            else {
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            return Promise.reject(response);
        }).then((data) => {
            setOtherProfileData(data);
            console.log(data);
        }).catch((error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error");
        });
    }
    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}
function setMyProfileData(data: any) {
    let nameSec = <HTMLElement>document.getElementById("myProfName");
    nameSec.innerHTML = "Name: " + data.mData.name;
    let emailSec = <HTMLElement>document.getElementById("myProfEmail");
    emailSec.innerHTML = "Email: " + data.mData.email;
    let sexualSec = <HTMLElement>document.getElementById("myProfSexual");
    sexualSec.innerHTML = "Sexual Identity: " + data.mData.sexual_identity;
    let genderSec = <HTMLElement>document.getElementById("myProfGender");
    genderSec.innerHTML = "Gender Orientation: " + data.mData.gender_identity;
    let bioSec = <HTMLElement>document.getElementById("myProfBio");
    bioSec.innerHTML = "Bio: " + data.mData.bio;
    myUser = new User(data.mData.uid, data.mData.name, data.mData.email, data.mData.sexual_identity, data.mData.gender_identity, data.mData.bio);
    console.log('myUser diff method = ' + myUser.name);
}

function setOtherProfileData(data: any) {
    let nameSec = <HTMLElement>document.getElementById("otherProfName");
    nameSec.innerHTML = "Name: " + data.mData.name;
    let emailSec = <HTMLElement>document.getElementById("otherProfEmail");
    emailSec.innerHTML = "Email: " + data.mData.email;
    let bioSec = <HTMLElement>document.getElementById("otherProfBio");
    bioSec.innerHTML = "Bio: " + data.mData.bio;
}

function hideEditProfileForm() {
    const all_profEditElements = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("editProfile"))
    for (let i = 0; i < all_profEditElements.length; ++i) {
        all_profEditElements[i].style.display = "none";
    }
}

function showEditProfileForm() {
    const all_profInfoElements = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("myProfileInfo"));
    for (let i = 0; i < all_profInfoElements.length; ++i) {
        all_profInfoElements[i].style.display = "none";
    }
    const all_profEditElements = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("editProfile"));
    for (let i = 0; i < all_profEditElements.length; ++i) {
        all_profEditElements[i].style.display = "block";
    }
}

class User {
    uid: number;
    name: string;
    email: string;
    gender_identity: string;
    sexual_identity: string;
    bio: string;
    constructor(userID: number, name: string, email: string, gender: string, sexual: string, bio: string) {
        this.uid = userID;
        this.name = name;
        this.email = email;
        this.gender_identity = gender;
        this.sexual_identity = sexual
        this.bio = bio;
        console.log('myUser = ' + this.name);
    }

}
class EditUserForm {
    constructor() {
        document.getElementById("SubmitEditProfileButton")?.addEventListener("click", (e) => { editUserForm.submitForm(); });
        document.getElementById("editProfileCancelButton")?.addEventListener("click", (e) => { editUserForm.clearForm(); });
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        showMyProfile();
        (<HTMLInputElement>document.getElementById("sexualIdentityInput")).value = "";
        (<HTMLInputElement>document.getElementById("genderOrientationInput")).value = "";
        (<HTMLInputElement>document.getElementById("bioInput")).value = "";
        const all_profInfoElements = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("myProfileInfo"));
        for (let i = 0; i < all_profInfoElements.length; ++i) {
            all_profInfoElements[i].style.display = "block";
        }
        const all_profEditElements = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("editProfile"));
        for (let i = 0; i < all_profEditElements.length; ++i) {
            all_profEditElements[i].style.display = "none";
        }

    }
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let sexualInput = "" + (<HTMLInputElement>document.getElementById("sexualIdentityInput")).value;
        let genderInput = "" + (<HTMLInputElement>document.getElementById("genderOrientationInput")).value;
        let bioInput = "" + (<HTMLInputElement>document.getElementById("bioInput")).value;
        // let editedUser = new User(myUser.uid, myUser.name, myUser.email, sexualInput, genderInput, bioInput);
        // console.log('myUser edit user method = ' + myUser.name);
        // console.log('edit user edit user method = ' + editedUser.name);

        // set up an AJAX PUT. 
        // When the server replies, the result will go to onSubmitResponse
        /**
         * Use fetch to make an http POST to the backend
         * @return resolved promise of JSON response (not quite sure what this means)
         */
        const doAjax = async () => {
            await fetch(`${backendUrl}/profile`, {
                method: 'PUT',
                body: JSON.stringify({
                    name: myUser.name,
                    email: myUser.email,
                    gender_identity: genderInput,
                    sexual_identity: sexualInput,
                    bio: bioInput
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                    'Authorization': cookieID
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                editUserForm.onSubmitResponse(data);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            editUserForm.clearForm();

        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    }
}


// Run some configuration code when the web page loads
document.addEventListener('DOMContentLoaded', () => {
    // Create the object that controls the "New Entry" form
    newEntryForm = new NewEntryForm();
    newCommentForm = new NewCommentForm();
    editUserForm = new EditUserForm();
    // Create the object for the main data list, and populate it with data from the server
    mainList = new ElementList();
    mainList.refresh();
    //window.alert('DOMContentLoaded');
    // Create the object that controls the "Edit Entry" form REMOVED
    // set up initial UI state
    // REMOVE (<HTMLElement>document.getElementById("editElement")).style.display = "none";
    (<HTMLElement>document.getElementById("myProfile")).style.display = "none";
    (<HTMLElement>document.getElementById("otherProfile")).style.display = "none";
    (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    (<HTMLElement>document.getElementById("addComment")).style.display = "none";
    (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    // set up the "Add Message" button
    document.getElementById("showFormButton")?.addEventListener("click", (e) => {
        (<HTMLElement>document.getElementById("addElement")).style.display = "block";
        (<HTMLElement>document.getElementById("showElements")).style.display = "none";
    });
    //Set up the Show my Profile button
    document.getElementById("showMyProfile")?.addEventListener("click", (e) => {
        showMyProfile();
        (<HTMLElement>document.getElementById("myProfile")).style.display = "block";
        hideEditProfileForm();
        (<HTMLElement>document.getElementById("showElements")).style.display = "none";
    });
    //set up button to get out of the my profile section
    document.getElementById("profileBackbutton")?.addEventListener("click", (e) => {
        (<HTMLElement>document.getElementById("myProfile")).style.display = "none";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    });
    //set up button to get out of the other profile section
    document.getElementById("otherProfileBackbutton")?.addEventListener("click", (e) => {
        (<HTMLElement>document.getElementById("otherProfile")).style.display = "none";
        (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    });
    //set up the edit profile button
    document.getElementById("editProfileButton")?.addEventListener("click", (e) => {
        showEditProfileForm();
    });

}, false);