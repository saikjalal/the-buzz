"use strict";
var describe;
var it;
var expect;
describe("Tests of interface button functions", function () {
    // test for hiding message list when adding message
    it("UI Test: Post a New Message Button Hides Listing", function () {
        // click the button for showing the add button
        document.getElementById("showFormButton").click();
        // expect that the post message form is not hidden
        expect(document.getElementById("addElement").style.display).toEqual("block");
        // expect that the element listing is hidden
        expect(document.getElementById("showElements").style.display).toEqual("none");
        // reset the UI, so we don't mess up the next test
        document.getElementById("addCancel").click();
    });
    // Test that pressing cancel in add interface hides add entry, and shows showElements
    it("UI Test: Cancelling in add interface hides add interface", function () {
        // click the button for showing the add button
        document.getElementById("showFormButton").click();
        document.getElementById("addCancel").click();
        // expect that the post message form is not hidden
        expect(document.getElementById("addElement").style.display).toEqual("none");
        // expect that the element listing is hidden
        expect(document.getElementById("showElements").style.display).toEqual("block");
    });
    // TODO: This test still doesn't work. I am able to access messageList, but querying for table comes up null
    // Test that posting add creates message
    it("UI/Logic Test: posting add creates new message", function () {
        // click the button for showing the add button
        document.getElementById("showFormButton").click();
        const newMessageInput = document.getElementById("newMessage");
        newMessageInput.value = "test message";
        document.getElementById("addButton").click();
        // expect element to be test
        const messageList = document.getElementById("messageList");
        const lastMessageRow = messageList.querySelector("table tr:last-child");
        const messageCell = lastMessageRow === null || lastMessageRow === void 0 ? void 0 : lastMessageRow.querySelector("td:first-child");
        expect(messageCell === null || messageCell === void 0 ? void 0 : messageCell.innerHTML).toEqual("test message");
    });
    // test that pressing like increments counter
    it("UI/Logic Test: Pressing like increments counter", function () {
        var _a, _b;
        // click the button for liking
        //(<HTMLElement>document.getElementById("messageList")?.querySelector("table tr:last-child")?.querySelector("td:fourth-child")).click();
        const messageList = document.getElementById("messageList");
        const lastMessageRow = messageList.querySelector("table tr:last-child");
        //lastMessageRow?.querySelector("td:fourth-child")?.click();
        // expect likes = 1
        expect(((_b = (_a = document.getElementById("messageList")) === null || _a === void 0 ? void 0 : _a.querySelector("table tr:last-child")) === null || _b === void 0 ? void 0 : _b.querySelector("td:second-child")).style.display).toEqual("Likes: 1");
    });
    //test that pressing dislike decrements counter
    it("UI/Logic Test: Pressing dislike decrements counter", function () {
        var _a, _b, _c, _d;
        // click the button for liking
        ((_b = (_a = document.getElementById("messageList")) === null || _a === void 0 ? void 0 : _a.querySelector("table tr:last-child")) === null || _b === void 0 ? void 0 : _b.querySelector("td:last-child")).click();
        // expect likes = 0
        expect(((_d = (_c = document.getElementById("messageList")) === null || _c === void 0 ? void 0 : _c.querySelector("table tr:last-child")) === null || _d === void 0 ? void 0 : _d.querySelector("td:second-child")).style.display).toEqual("Likes: 0");
    });
    it("UI/Logic Test: deleting post removes message", function () {
        var _a, _b, _c;
        // click the button for removing
        ((_b = (_a = document.getElementById("messageList")) === null || _a === void 0 ? void 0 : _a.querySelector("table tr:last-child")) === null || _b === void 0 ? void 0 : _b.querySelector("td:second-child")).click();
        // expect number of elements in messageList to be zero? not sure if this will work exactly
        expect((_c = document.getElementById("messageList")) === null || _c === void 0 ? void 0 : _c.childElementCount).toEqual(0);
    });
    /////PHASE 2 Tests
    // Test that pressing cancel in add interface hides add entry, and shows showElements
    it("UI Test: The my profile button should show profile and hide elements", function () {
        // click the button for showing the my profile  button
        document.getElementById("showMyProfile").click();
        //(<HTMLElement>document.getElementById("addCancel")).click();
        // expect that the post message form is not hidden
        expect(document.getElementById("showElements").style.display).toEqual("none");
        // expect that the element listing is hidden
        expect(document.getElementById("myProfile").style.display).toEqual("block");
    });
    // Test that pressing comment brings up the comment form
    it("UI Test: The comment button should have the form pop up", function () {
        // click the button for showing the my profile  button
        document.getElementById("editProfileButton").click();
        //(<HTMLElement>document.getElementById("addCancel")).click();
        // expect that the post message form is not hidden
        expect(document.getElementById("sexualIdentityInput").style.display).toEqual("block");
        // expect that the element listing is hidden
        expect(document.getElementById("myProfSexual").style.display).toEqual("none");
    });
    //Test for checking if links can be posted on messages
    it("UI Test: Test that links are able to be posted on messages", function () {
    });

    // test for adding file on message
    it("UI Test: Add file to message", function () {
        // Click on the "New Message" button to open the message composer
        document.getElementById("newMessageButton").click();
        // Fill in the message text input with some text
        const messageInput = document.getElementById("messageInput");
        messageInput.value = "This is a test message.";
        // Attach a file to the message
        const fileInput = document.getElementById("fileInput");
        const testFile = new File(["test file contents"], "test.txt", { type: "text/plain" });
        // Click on the send message button
        document.getElementById("sendMessageButton").click();
        // Verify that the message with the attached file was sent
        const sentMessage = document.querySelector(".message.sent");
        //expect(sentMessage.querySelector(".message-text").textContent).toEqual("This is a test message.");
        //expect(sentMessage.querySelector(".message-file").textContent).toContain("test.txt");
    });

    // test for files on messages
    it("UI Test: Test that checks if files option available on messages", function () {
        // click the button for showing the my profile  button
        document.getElementById("uploadFileButton").click();
        //(<HTMLElement>document.getElementById("addCancel")).click();
        // expect that the post message form is not hidden
        expect(document.getElementById("").style.display).toEqual("block");
        // expect that the element listing is hidden
        expect(document.getElementById("myProfSexual").style.display).toEqual("none");
    });
});
