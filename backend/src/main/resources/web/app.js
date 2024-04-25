"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $;
// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var newEntryForm;
var newCommentForm;
var editUserForm;
//This is only the local server. 
// This constant indicates the path to our back-end server (change to your own)
var backendUrl = "http://localhost:4567"; //"https://2023sp-softserve.dokku.cse.lehigh.edu";
var sessionID = 'testToken';
var cookieID; //session id for the user
var cookieUser; // user of the logged in user
var myUser; // just a user class to hold their info
// var files:string; // file 
//Handles the google sign in button and recieves all the information like the userid and google information.
// Will send a post request to backend for authenication.
function handleCredentialResponse(response) {
    var _this = this;
    var profile = decodeJwtResponse(response.credential);
    console.log('ID: ' + profile.sub);
    console.log('Name: ' + profile.name);
    console.log('Image URL: ' + profile.picture);
    console.log('Email: ' + profile.email);
    var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/session_authenticate"), {
                        method: 'POST',
                        body: JSON.stringify({
                            oauth_token: response.credential
                        }),
                        headers: {
                            'Content-type': 'application/json; charset=UTF-8'
                        }
                    }).then(function (response) {
                        // If we get an "ok" message, return the json
                        console.log(response);
                        if (response.ok) {
                            return Promise.resolve(response.json());
                        }
                        // Otherwise, handle server errors with a detailed popup message
                        else {
                            window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                        }
                        return Promise.reject(response);
                    }).then(function (data) {
                        sessionID = data.mData.sessionToken;
                        var dataUserID = data.mData.userID;
                        console.log('user id' + sessionID);
                        document.cookie = 'userID=' + dataUserID;
                        document.cookie = 'sessionID=' + sessionID;
                        console.log(document.cookie);
                        if (sessionID != null) {
                            // window.location.href = "http://localhost:8080";
                            window.location.href = "http://localhost:4567";
                        }
                        // console.log("Returned ID " + sessionID);
                    }).catch(function (error) {
                        console.warn('Something went wrong.', error);
                        window.alert("Unspecified error");
                    })];
                case 1:
                    _a.sent();
                    return [2 /*return*/];
            }
        });
    }); };
    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}
function decodeJwtResponse(data) {
    var tokens = data.split(".");
    return JSON.parse(atob(tokens[1]));
}
//Cookie function to save the session id needed for Authorization
function getCookie(dataName) {
    var id = dataName + '=';
    var decodedCookie = decodeURIComponent(document.cookie);
    var cookies = decodedCookie.split(';');
    for (var i = 0; i < cookies.length; i++) {
        var c = cookies[i];
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
var NewEntryForm = /** @class */ (function () {
    /**
     * To initialize the object, we say what method of NewEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    function NewEntryForm() {
        var _a, _b;
        (_a = document.getElementById("addCancel")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", function (e) { newEntryForm.clearForm(); });
        (_b = document.getElementById("addButton")) === null || _b === void 0 ? void 0 : _b.addEventListener("click", function (e) { newEntryForm.submitForm(); });
    }
    /**
     * Clear the form's input fields
     */
    NewEntryForm.prototype.clearForm = function () {
        document.getElementById("newMessage").value = "";
        // reset the UI
        // REMOVE (<HTMLElement>document.getElementById("editElement")).style.display = "none";
        document.getElementById("addElement").style.display = "none";
        document.getElementById("showElements").style.display = "block";
    };
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    NewEntryForm.prototype.submitForm = function () {
        var _this = this;
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        var msg = "" + document.getElementById("newMessage").value;
        if (msg === "") {
            window.alert("Error: title or message is not valid");
            return;
        }
        //adding a link to messages and the url can be found using regex. 
        // Extract link from the message using regex
        var linkRegex = /(http|ftp|https):\/\/([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:\/~+#-]*[\w@?^=%&\/~+#-])/; // uses this regex to find url 
        var linkMatch = msg.match(linkRegex);
        var link = linkMatch ? linkMatch[0] : null;
        // 
        var linkData = new FormData();
        linkData.append("mMessage", msg);
        if (link) {
            linkData.append("mLink", link);
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
        //submitting a file in a message? ? ? 
        var fileInput = document.getElementById("fileInput"); //document.getElementById("fileInput") as HTMLInputElement;
        if (fileInput != null) {
            // let file = fileInput.files[0]; // possibly null object still ? getting error stating that it is still null even after checking that it isn't null
        }
        else {
            window.alert("Error: File is empty");
            return;
        }
        var formData = new FormData();
        formData.append("mMessage", msg);
        //formData.append("mFile", file);
        //some lines commented out because of null error. Should be fine. 
        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        /**
         * Use fetch to make an http POST to the backend
         * @return resolved promise of JSON response (not quite sure what this means)
         */
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/messages"), {
                            method: 'POST',
                            body: JSON.stringify({
                                mMessage: msg
                            }),
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            // If we get an "ok" message, return the json
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            // Otherwise, handle server errors with a detailed popup message
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            newEntryForm.onSubmitResponse(data);
                            mainList.refresh();
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     * @param data The object returned by the server
     */
    NewEntryForm.prototype.onSubmitResponse = function (data) {
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
    };
    return NewEntryForm;
}()); // end class NewEntryForm
//////////////// Start of New Comment Form ///////////////////////
var NewCommentForm = /** @class */ (function () {
    /**
     * To initialize the object, we say what method of NewCommentForm should be
     * run in response to each of the form's buttons being clicked.
     */
    function NewCommentForm() {
        var _a;
        (_a = document.getElementById("addCommentCancel")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", function (e) { newCommentForm.clearCommentForm(); });
    }
    /**
     * Clear the comment form's input fields
     */
    NewCommentForm.prototype.clearCommentForm = function () {
        document.getElementById("newComment").value = "";
        // reset the UI
        // REMOVE (<HTMLElement>document.getElementById("editElement")).style.display = "none";
        document.getElementById("addComment").style.display = "none";
        document.getElementById("showElements").style.display = "block";
    };
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    NewCommentForm.prototype.submitCommentForm = function (msgID) {
        // for links and messages, getting the values and then checking if their empty or not
        var _this = this;
        // document.getElementById("addCommentButton")?.removeEventListener("click", newCommentForm.submitCommentForm);
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        var msg = "" + document.getElementById("newComment").value; // new message
        var linkField = document.getElementById("commentLink");
        var link = "" + linkField.value;
        var imgField = document.getElementById("commentImg");
        var img = "" + imgField.value;
        //Extract URLS via regex
        var urlRegex = /(http|ftp|https):\/\/([\w_-]+(?:(?:\.[\w_-]+)+))([\w.,@?^=%&:\/~+#-]*[\w@?^=%&\/~+#-])/;
        var urls = msg.match(urlRegex);
        //provide image tags
        //will be searchable via regex
        var commentText = msg.replace(urlRegex, '<a href="$1" target="_blank">$1</a>');
        if (link) {
            commentText += " <a href=\"".concat(link, "\" target=\"_blank\">").concat(link, "</a>");
        }
        if (img) {
            commentText += " <img src=\"".concat(img, "\" alt=\"Image\">");
        }
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
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/comment"), {
                            method: 'POST',
                            body: JSON.stringify({
                                messageId: msgID,
                                comment: msg
                            }),
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            // If we get an "ok" message, return the json
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            // Otherwise, handle server errors with a detailed popup message
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            newCommentForm.onSubmitResponseComment(data, msgID);
                            // mainList.refresh();
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    NewCommentForm.prototype.submitEditCommentForm = function (msgID, commentID) {
        var _this = this;
        // document.getElementById("addCommentButton")?.removeEventListener("click", newCommentForm.submitCommentForm);
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        var msg = "" + document.getElementById("newComment").value;
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
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/comment/").concat(commentID), {
                            method: 'PUT',
                            body: JSON.stringify({
                                messageId: msgID,
                                comment: msg
                            }),
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            // If we get an "ok" message, return the json
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            // Otherwise, handle server errors with a detailed popup message
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            newCommentForm.onSubmitResponseComment(data, msgID);
                            // mainList.refreshCmnt(msgID);
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
    * onSubmitResponseComment runs when the AJAX call in submitCommentForm() returns a
    * result.
    * @param data The object returned by the server
    */
    NewCommentForm.prototype.onSubmitResponseComment = function (data, msgID) {
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
    };
    return NewCommentForm;
}()); // END new Comment Form
///////////////////////////// Element List Start ////////////////////////////
// a global for the main ElementList of the program.  See newEntryForm for 
// explanation
var mainList;
/**
 * ElementList provides a way of seeing all of the data stored on the server.
 */
var ElementList = /** @class */ (function () {
    function ElementList() {
    }
    /**
     * refresh is the public method for updating messageList
     */
    ElementList.prototype.refresh = function () {
        var _this = this;
        // Issue an AJAX GET and then pass the result to update(). 
        console.log("messages Session ID " + cookieID + ' userid ' + cookieUser);
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/messages"), {
                            method: 'GET',
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            // If we get an "ok" message, clear the form
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            // Otherwise, handle server errors with a detailed popup message
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            mainList.update(data);
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * refreshCmnt is the public method for updating messageList with comments
     */
    ElementList.prototype.refreshCmnt = function (msgID) {
        var _this = this;
        // Issue an AJAX GET and then pass the result to update(). 
        // console.log("messages Session ID " + cookieID);
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/messages"), {
                            method: 'GET',
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            // If we get an "ok" message, clear the form
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            // Otherwise, handle server errors with a detailed popup message
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            mainList.updateCmnt(data, msgID);
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * Update iterates through the list of messages, displays each message and its like count, and creates the buttons for liking, disliking, and deleting esach message
     * @param data messages data passed from GET request to backend
     */
    ElementList.prototype.update = function (data) {
        console.log(data);
        var elem_messageList = document.getElementById("messageList");
        if (elem_messageList !== null) {
            elem_messageList.innerHTML = "";
            var fragment = document.createDocumentFragment();
            var table = document.createElement('table');
            var tr = document.createElement('tr');
            var th_user = document.createElement('th');
            var th_msgTitle = document.createElement('th');
            th_user.innerHTML = "User #";
            th_msgTitle.innerHTML = "Message";
            th_msgTitle.setAttribute('colspan', "6");
            tr.appendChild(th_user);
            tr.appendChild(th_msgTitle);
            table.appendChild(tr);
            for (var i = 0; i < data.mData.length; i++) { //let i = data.mData.length -1 ; i >= 0; --i
                var tr_1 = document.createElement('tr');
                tr_1.setAttribute('msg-id', data.mData[i].mId);
                var td_owner = document.createElement('td');
                td_owner.classList.add('viewProfile');
                var td_message = document.createElement('td');
                //let td_id = document.createElement('td');
                var td_likes = document.createElement('td');
                // TODO: should this be mContent, when I'm supposed to put mMessage above?
                td_owner.innerHTML = data.mData[i].userID;
                td_message.innerHTML = data.mData[i].mContent;
                //td_id.innerHTML = data.mData[i].mId;
                td_likes.innerHTML = "Likes: " + data.mData[i].mLikes;
                //tr.appendChild(td_id);
                tr_1.appendChild(td_owner);
                tr_1.appendChild(td_message);
                tr_1.appendChild(td_likes);
                tr_1.appendChild(this.buttons(data.mData[i].mId, false));
                table.appendChild(tr_1);
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
        var all_likebtns = document.getElementsByClassName("likebtn");
        for (var i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].addEventListener("click", function (e) { mainList.clickLike(e); });
        }
        /**
         * Iterates through each dislike button and adds an event listener, calling clickDislike on that message if pressed
         */
        var all_dlbtns = document.getElementsByClassName("dlbtn");
        for (var i = 0; i < all_dlbtns.length; ++i) {
            all_dlbtns[i].addEventListener("click", function (e) { mainList.clickDislike(e); });
        }
        /**
         * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
         */
        var all_showCommentbtns = document.getElementsByClassName("cmntbtn");
        for (var i = 0; i < all_showCommentbtns.length; ++i) {
            all_showCommentbtns[i].addEventListener("click", function (e) { mainList.clickShow(e); });
        }
        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        */
        var all_addCommentbtns = document.getElementsByClassName("addcmntbtn");
        for (var i = 0; i < all_addCommentbtns.length; ++i) {
            all_addCommentbtns[i].addEventListener("click", function (e) { mainList.clickAddCmnt(e); });
        }
        /**
        * Iterates through each of the table data blocks that show the user id so that you can click them and see the profiles
        */
        var all_userIdBlocks = document.getElementsByClassName("viewProfile");
        for (var i = 0; i < all_userIdBlocks.length; ++i) {
            all_userIdBlocks[i].addEventListener("click", function (e) { mainList.getUserID(e); });
        }
    };
    /**
     * UpdateCmnt will do the same as Update above but will instead include the comments for the choosen message
     * @param data messages data passed from GET request to backend
     * @param msgID message id of the message you want to show the comments for
     */
    ElementList.prototype.updateCmnt = function (data, msgID) {
        console.log(data);
        var elem_messageList = document.getElementById("messageList");
        if (elem_messageList !== null) {
            elem_messageList.innerHTML = "";
            var fragment = document.createDocumentFragment();
            var table = document.createElement('table');
            for (var i = 0; i < data.mData.length; i++) { //let i = data.mData.length -1 ; i >= 0; --i
                var rowID = data.mData[i].mId;
                var tr = document.createElement('tr');
                tr.setAttribute('msg-id', data.mData[i].mId);
                var td_owner = document.createElement('td');
                td_owner.classList.add('viewProfile');
                var td_message = document.createElement('td');
                //let td_id = document.createElement('td');
                var td_likes = document.createElement('td');
                // TODO: should this be mContent, when I'm supposed to put mMessage above?
                td_owner.innerHTML = data.mData[i].userID;
                td_message.innerHTML = data.mData[i].mContent;
                //td_id.innerHTML = data.mData[i].mId;
                td_likes.innerHTML = "Likes: " + data.mData[i].mLikes;
                //tr.appendChild(td_id);
                tr.appendChild(td_owner);
                tr.appendChild(td_message);
                tr.appendChild(td_likes);
                if (rowID == msgID) {
                    tr.appendChild(this.buttons(data.mData[i].mId, true));
                }
                else {
                    tr.appendChild(this.buttons(data.mData[i].mId, false));
                }
                table.appendChild(tr);
                if (rowID == msgID) { // If the messageID is equal to the message it is currently creating then it should also add/create its comments
                    for (var j = data.mData[i].mComments.length - 1; j >= 0; --j) { //let j = 0; j < data.mData[i].mComments.length; j++)
                        var tr_2 = document.createElement('tr');
                        tr_2.setAttribute('msg-id', data.mData[i].mId);
                        tr_2.setAttribute('comment-id', data.mData[i].mComments[j].commentID);
                        var td_owner_1 = document.createElement('td');
                        td_owner_1.classList.add('viewProfile');
                        var td_message_1 = document.createElement('td');
                        td_owner_1.innerHTML = data.mData[i].mComments[j].userID;
                        td_message_1.innerHTML = data.mData[i].mComments[j].comment;
                        tr_2.appendChild(td_owner_1);
                        tr_2.appendChild(td_message_1);
                        tr_2.appendChild(this.cmntButtons(data.mData[i].mId, data.mData[i].mComments[j].commentID));
                        table.appendChild(tr_2);
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
        var all_likebtns = document.getElementsByClassName("likebtn");
        for (var i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].addEventListener("click", function (e) { mainList.clickLike(e); });
        }
        /**
         * Iterates through each dislike button and adds an event listener, calling clickDislike on that message if pressed
         */
        var all_dlbtns = document.getElementsByClassName("dlbtn");
        for (var i = 0; i < all_dlbtns.length; ++i) {
            all_dlbtns[i].addEventListener("click", function (e) { mainList.clickDislike(e); });
        }
        /**
         * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
         */
        var all_showCommentbtns = document.getElementsByClassName("cmntbtn");
        for (var i = 0; i < all_showCommentbtns.length; ++i) {
            all_showCommentbtns[i].addEventListener("click", function (e) { mainList.clickShow(e); });
        }
        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        */
        var all_addCommentbtns = document.getElementsByClassName("addcmntbtn");
        for (var i = 0; i < all_addCommentbtns.length; ++i) {
            all_addCommentbtns[i].addEventListener("click", function (e) { mainList.clickAddCmnt(e); });
        }
        /**
        * Iterates through each show comment button and adds an event listener, calling clickShow on that message if pressed
        */
        var all_editCommentbtns = document.getElementsByClassName("editcmntbtn");
        for (var i = 0; i < all_editCommentbtns.length; ++i) {
            all_editCommentbtns[i].addEventListener("click", function (e) { mainList.clickEditCmnt(e); });
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
        var all_userIdBlocks = document.getElementsByClassName("viewProfile");
        for (var i = 0; i < all_userIdBlocks.length; ++i) {
            all_userIdBlocks[i].addEventListener("click", function (e) { mainList.getUserID(e); });
        }
    };
    /**
     * buttons() adds a 'delete', 'like', and 'dislike' button to the HTML for each row
     * @param id of the message you are currently setting up the buttons for
     * @param showingCmnt check to see if the message is showing its comments. Depening on this the show comments button will display Show more or Show Less
     * @return DocumentFragment containing delete, like, and dislike buttons
     */
    ElementList.prototype.buttons = function (id, showingCmnt) {
        var fragment = document.createDocumentFragment();
        var td = document.createElement('td');
        // create edit button, add to new td, add td to returned fragment; REMOVED
        var btn = document.createElement('button');
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
        }
        else {
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
    };
    /**
      * cmntButtons() adds a 'delete' and 'Edit' button to each comment row that is being displayed
      * @param id of the comment you are currently setting up the buttons for
      * @return DocumentFragment containing delete, like, and dislike buttons
      */
    ElementList.prototype.cmntButtons = function (msgID, cmntID) {
        var fragment = document.createDocumentFragment();
        var td = document.createElement('td');
        // create edit button, add to new td, add td to returned fragment; REMOVED
        var btn = document.createElement('button');
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
    };
    /**
     * clickDelCmnt is the response to a click of a Delete comment button
     */
    ElementList.prototype.clickDelCmnt = function (e) {
        var _this = this;
        var btn = e.target;
        var messageID = Number(btn.getAttribute("msg-value"));
        var commentID = Number(btn.getAttribute("cmnt-value"));
        // Issue an AJAX DELETE and then invoke refresh()
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/comment/").concat(commentID), {
                            method: 'DELETE',
                            body: JSON.stringify({
                                messageId: messageID,
                                comment_id: commentID
                            }),
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            mainList.refreshCmnt(messageID);
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * clickEditCmnt is the response to a click of a Edit comment button
     */
    ElementList.prototype.clickEditCmnt = function (e) {
        var _a;
        var btn = e.target;
        var messageID = Number(btn.getAttribute("msg-value"));
        var commentID = Number(btn.getAttribute("cmnt-value"));
        document.getElementById("addComment").style.display = "block";
        document.getElementById("showElements").style.display = "none";
        (_a = document.getElementById("addCommentButton")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", function (e) { newCommentForm.submitEditCommentForm(messageID, commentID); }, { once: true });
    };
    /**
     * clickAddCmnt is the response to a click of a Show Comment button, will open up the comment form that will then send a request
     */
    ElementList.prototype.clickAddCmnt = function (e) {
        var _a;
        var btn = e.target;
        var messageRowID = Number(btn.getAttribute("data-value"));
        document.getElementById("addComment").style.display = "block";
        document.getElementById("showElements").style.display = "none";
        (_a = document.getElementById("addCommentButton")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", function (e) { newCommentForm.submitCommentForm(messageRowID); }, { once: true });
    };
    /**
     * clickShow is the response to a click of a Show Less/More button, will make the message comment appear or go away
     */
    ElementList.prototype.clickShow = function (e) {
        var btn = e.target;
        if (btn.innerHTML == 'Show Less') {
            mainList.refresh();
        }
        else {
            var messageRowID = Number(btn.getAttribute("data-value"));
            mainList.refreshCmnt(messageRowID);
        }
    };
    /**
     * clickDelete is the response to a click of a delete button, and send an HTTP DELETE to the message of the given ID
     */
    ElementList.prototype.clickDelete = function (e) {
        var _this = this;
        var id = e.target.getAttribute("data-value");
        // Issue an AJAX DELETE and then invoke refresh()
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/messages/").concat(id), {
                            method: 'DELETE',
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            mainList.refresh();
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * clickLike is the response to a click of a delete button, and sends an HTTP PUT to like the message of the given ID
     */
    ElementList.prototype.clickLike = function (e) {
        var _this = this;
        // get the id of the row
        var id = e.target.getAttribute("data-value");
        // Issue an AJAX PUT and then invoke refresh()
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/messages/l/").concat(id), {
                            method: 'PUT',
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            mainList.refresh();
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * clickDislike is the response to a click of a delete button, and sends an HTTP PUT to like the message of the given ID
     */
    ElementList.prototype.clickDislike = function (e) {
        var _this = this;
        // get the id of the row
        var id = e.target.getAttribute("data-value");
        // Issue an AJAX PUT and then invoke refresh()
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/messages/d/").concat(id), {
                            method: 'PUT',
                            headers: {
                                'Content-type': 'application/json; charset=UTF-8',
                                'Authorization': cookieID
                            }
                        }).then(function (response) {
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            mainList.refresh();
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    ElementList.prototype.getUserID = function (e) {
        var tableBlock = e.target;
        var userNum = tableBlock.innerHTML;
        console.log("Messages User id is: " + userNum);
        showOtherProfile(Number(userNum));
        document.getElementById("otherProfile").style.display = "block";
        document.getElementById("showElements").style.display = "none";
    };
    return ElementList;
}()); ///////// end class ElementList /////////////////////////////
//for phase 3, don't need to write any code or alter code in profile section
/////////// Start of Profile Functions ////////////////
function showMyProfile() {
    var _this = this;
    var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/profile/").concat(cookieUser), {
                        method: 'GET',
                        headers: {
                            'Content-type': 'application/json; charset=UTF-8',
                            'Authorization': cookieID
                        }
                    }).then(function (response) {
                        if (response.ok) {
                            return Promise.resolve(response.json());
                        }
                        else {
                            window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                        }
                        return Promise.reject(response);
                    }).then(function (data) {
                        setMyProfileData(data);
                        console.log(data);
                    }).catch(function (error) {
                        console.warn('Something went wrong.', error);
                        window.alert("Unspecified error");
                    })];
                case 1:
                    _a.sent();
                    return [2 /*return*/];
            }
        });
    }); };
    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}
function showOtherProfile(userNum) {
    var _this = this;
    var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/profile/").concat(userNum), {
                        method: 'GET',
                        headers: {
                            'Content-type': 'application/json; charset=UTF-8',
                            'Authorization': cookieID
                        }
                    }).then(function (response) {
                        if (response.ok) {
                            return Promise.resolve(response.json());
                        }
                        else {
                            window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                        }
                        return Promise.reject(response);
                    }).then(function (data) {
                        setOtherProfileData(data);
                        console.log(data);
                    }).catch(function (error) {
                        console.warn('Something went wrong.', error);
                        window.alert("Unspecified error");
                    })];
                case 1:
                    _a.sent();
                    return [2 /*return*/];
            }
        });
    }); };
    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
}
function setMyProfileData(data) {
    var nameSec = document.getElementById("myProfName");
    nameSec.innerHTML = "Name: " + data.mData.name;
    var emailSec = document.getElementById("myProfEmail");
    emailSec.innerHTML = "Email: " + data.mData.email;
    var sexualSec = document.getElementById("myProfSexual");
    sexualSec.innerHTML = "Sexual Identity: " + data.mData.sexual_identity;
    var genderSec = document.getElementById("myProfGender");
    genderSec.innerHTML = "Gender Orientation: " + data.mData.gender_identity;
    var bioSec = document.getElementById("myProfBio");
    bioSec.innerHTML = "Bio: " + data.mData.bio;
    myUser = new User(data.mData.uid, data.mData.name, data.mData.email, data.mData.sexual_identity, data.mData.gender_identity, data.mData.bio);
    console.log('myUser diff method = ' + myUser.name);
}
function setOtherProfileData(data) {
    var nameSec = document.getElementById("otherProfName");
    nameSec.innerHTML = "Name: " + data.mData.name;
    var emailSec = document.getElementById("otherProfEmail");
    emailSec.innerHTML = "Email: " + data.mData.email;
    var bioSec = document.getElementById("otherProfBio");
    bioSec.innerHTML = "Bio: " + data.mData.bio;
}
function hideEditProfileForm() {
    var all_profEditElements = document.getElementsByClassName("editProfile");
    for (var i = 0; i < all_profEditElements.length; ++i) {
        all_profEditElements[i].style.display = "none";
    }
}
function showEditProfileForm() {
    var all_profInfoElements = document.getElementsByClassName("myProfileInfo");
    for (var i = 0; i < all_profInfoElements.length; ++i) {
        all_profInfoElements[i].style.display = "none";
    }
    var all_profEditElements = document.getElementsByClassName("editProfile");
    for (var i = 0; i < all_profEditElements.length; ++i) {
        all_profEditElements[i].style.display = "block";
    }
}
var User = /** @class */ (function () {
    function User(userID, name, email, gender, sexual, bio) {
        this.uid = userID;
        this.name = name;
        this.email = email;
        this.gender_identity = gender;
        this.sexual_identity = sexual;
        this.bio = bio;
        console.log('myUser = ' + this.name);
    }
    return User;
}());
var EditUserForm = /** @class */ (function () {
    function EditUserForm() {
        var _a, _b;
        (_a = document.getElementById("SubmitEditProfileButton")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", function (e) { editUserForm.submitForm(); });
        (_b = document.getElementById("editProfileCancelButton")) === null || _b === void 0 ? void 0 : _b.addEventListener("click", function (e) { editUserForm.clearForm(); });
    }
    /**
     * Clear the form's input fields
     */
    EditUserForm.prototype.clearForm = function () {
        showMyProfile();
        document.getElementById("sexualIdentityInput").value = "";
        document.getElementById("genderOrientationInput").value = "";
        document.getElementById("bioInput").value = "";
        var all_profInfoElements = document.getElementsByClassName("myProfileInfo");
        for (var i = 0; i < all_profInfoElements.length; ++i) {
            all_profInfoElements[i].style.display = "block";
        }
        var all_profEditElements = document.getElementsByClassName("editProfile");
        for (var i = 0; i < all_profEditElements.length; ++i) {
            all_profEditElements[i].style.display = "none";
        }
    };
    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    EditUserForm.prototype.submitForm = function () {
        var _this = this;
        window.alert("Submit form called.");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        var sexualInput = "" + document.getElementById("sexualIdentityInput").value;
        var genderInput = "" + document.getElementById("genderOrientationInput").value;
        var bioInput = "" + document.getElementById("bioInput").value;
        // let editedUser = new User(myUser.uid, myUser.name, myUser.email, sexualInput, genderInput, bioInput);
        // console.log('myUser edit user method = ' + myUser.name);
        // console.log('edit user edit user method = ' + editedUser.name);
        // set up an AJAX PUT. 
        // When the server replies, the result will go to onSubmitResponse
        /**
         * Use fetch to make an http POST to the backend
         * @return resolved promise of JSON response (not quite sure what this means)
         */
        var doAjax = function () { return __awaiter(_this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, fetch("".concat(backendUrl, "/profile"), {
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
                        }).then(function (response) {
                            // If we get an "ok" message, return the json
                            if (response.ok) {
                                return Promise.resolve(response.json());
                            }
                            // Otherwise, handle server errors with a detailed popup message
                            else {
                                window.alert("The server replied not ok: ".concat(response.status, "\n") + response.statusText);
                            }
                            return Promise.reject(response);
                        }).then(function (data) {
                            editUserForm.onSubmitResponse(data);
                            console.log(data);
                        }).catch(function (error) {
                            console.warn('Something went wrong.', error);
                            window.alert("Unspecified error");
                        })];
                    case 1:
                        _a.sent();
                        return [2 /*return*/];
                }
            });
        }); };
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     * @param data The object returned by the server
     */
    EditUserForm.prototype.onSubmitResponse = function (data) {
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
    };
    return EditUserForm;
}());
// Run some configuration code when the web page loads
document.addEventListener('DOMContentLoaded', function () {
    var _a, _b, _c, _d, _e;
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
    document.getElementById("myProfile").style.display = "none";
    document.getElementById("otherProfile").style.display = "none";
    document.getElementById("addElement").style.display = "none";
    document.getElementById("addComment").style.display = "none";
    document.getElementById("showElements").style.display = "block";
    // set up the "Add Message" button
    (_a = document.getElementById("showFormButton")) === null || _a === void 0 ? void 0 : _a.addEventListener("click", function (e) {
        document.getElementById("addElement").style.display = "block";
        document.getElementById("showElements").style.display = "none";
    });
    //Set up the Show my Profile button
    (_b = document.getElementById("showMyProfile")) === null || _b === void 0 ? void 0 : _b.addEventListener("click", function (e) {
        showMyProfile();
        document.getElementById("myProfile").style.display = "block";
        hideEditProfileForm();
        document.getElementById("showElements").style.display = "none";
    });
    //set up button to get out of the my profile section
    (_c = document.getElementById("profileBackbutton")) === null || _c === void 0 ? void 0 : _c.addEventListener("click", function (e) {
        document.getElementById("myProfile").style.display = "none";
        document.getElementById("showElements").style.display = "block";
    });
    //set up button to get out of the other profile section
    (_d = document.getElementById("otherProfileBackbutton")) === null || _d === void 0 ? void 0 : _d.addEventListener("click", function (e) {
        document.getElementById("otherProfile").style.display = "none";
        document.getElementById("showElements").style.display = "block";
    });
    //set up the edit profile button
    (_e = document.getElementById("editProfileButton")) === null || _e === void 0 ? void 0 : _e.addEventListener("click", function (e) {
        showEditProfileForm();
    });
}, false);
