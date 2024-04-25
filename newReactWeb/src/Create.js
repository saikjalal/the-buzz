import { useState } from "react";
import { useHistory } from "react-router-dom";
const backendUrl = "http://2023sp-softserve.dokku.cse.lehigh.edu";

const Create = () => {
  const [message, setMessage] = useState('');
  const history = useHistory();

  const handleSubmit = (e) => {
    e.preventDefault();
    const message = { message };

    fetch(backendUrl + '/messages', {
      method: 'POST',
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(message)
    }).then(() => {
      // history.go(-1);
      history.push('/');
    })
  }

  return (
    <div className="create">
      <h2>Add a New Message</h2>
      <form onSubmit={handleSubmit}>
        <label>Message content:</label>
        <input 
          type="text" 
          required 
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />
        <button>Add Message</button>
      </form>
    </div>
  );
}
 
export default Create;