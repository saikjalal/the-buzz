// // Prevent compiler errors when using jQuery.  "$" will be given a type of 
// // "any", so that we can use it anywhere, and assume it has any fields or
// // methods, without the compiler producing an error.
// var $: any;
// const backendUrl = "http://2023sp-softserve.dokku.cse.lehigh.edu";
// var sessionID;
// // export {sessionID};
// //Handles the google sign in button and recieves all the information like the userid and google information.
// // Will send a post request to backend for authenication.
// function handleCredentialResponse(response) {
//     var profile = decodeJwtResponse(response.credential);
//     console.log('ID: ' + profile.sub);
//     console.log('Name: ' + profile.name);
//     console.log('Image URL: ' + profile.picture);
//     console.log('Email: ' + profile.email);

//     const doAjax = async () => {
//       await fetch(`${backendUrl}/session_authenticate`, {
//             method: 'POST',
//             body: JSON.stringify({
//                 oath_token: response.credential
//             }),
//             headers: {
//                 'Content-type': 'application/json; charset=UTF-8'
//             }
//       }).then( (response) => {
//           // If we get an "ok" message, return the json
//           console.log(response);
//           if (response.ok) {
//               return Promise.resolve( response.json() );
//           }
//           // Otherwise, handle server errors with a detailed popup message
//           else{
//               window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
//           }
//           return Promise.reject(response);
//       }).then( (data) => {
//             sessionID = data.mData;
//             if(sessionID != null){
//                 window.location.href = "http://localhost:8080";
//             }
//             console.log(sessionID);
//       }).catch( (error) => {
//           console.warn('Something went wrong.', error);
//           window.alert("Unspecified error");
//       });
//     }
//       // make the AJAX post and output value or error message to console
//       doAjax().then(console.log).catch(console.log);
//   }    

// function decodeJwtResponse(data){
//   var tokens = data.split(".");
//   return JSON.parse(atob(tokens[1]));
// }