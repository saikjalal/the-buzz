import { useState, useEffect } from 'react';
const backendUrl = "http://2023sp-softserve.dokku.cse.lehigh.edu";

const Home = () => {
  const useGet = (url) => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    fetch(`${backendUrl}/messages`, { // had to remove this to get rid of typescript error
      method: 'GET',
      headers: {
          'Content-type': 'application/json; charset=UTF-8'
      }
    }).then((data) => {
      return messages.json()
    }).then((data) => {
      this.setState({
        messages: data.mData
      });
      //setData(data);
    }).catch(err => {
      if (err.name === 'AbortError') {
        console.log('fetch aborted')
      } else {
        // auto catches network / connection error
        setError(err.message);
      }
    });
    return { data, error };
  }

  const MessageList = ({ messages }) => {

    return (
      <div className="message-list">
        {messages.map(message => (
          <div className="message-preview" key={message.mId} >
            <h2>{message.mContent}</h2>
            <p>{message.mLikes}</p>
          </div>
        ))}
      </div>
    );
  }
  
  const { error, data: messages } = useGet(backendUrl + "/messages")

  return (
    <div className="home">
      {error && <div>{error}</div>}
      {messages && <MessageList messages={messages} />}
    </div>
  );
}
 
export default Home;