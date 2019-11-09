import React from 'react';
import logo from './logo.svg';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom'
import ListPropertiesComponent from './component/ListPropertiesComponent';

function App() {
  return (
    <div className="App">
      <Router>
        <Route path="/" exact component={ListPropertiesComponent} />
      </Router>
    </div>
  );
}

export default App;
