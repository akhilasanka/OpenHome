import React from 'react';
import logo from './logo.svg';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Main from './component/Main';


function App() {
  return (
    <div className="App">
      <Router>
        <Main/>
      </Router>
    </div>
  );
}

export default App;
