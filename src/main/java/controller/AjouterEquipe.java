import React, { useState } from "react";

function TodoApp() {
  const [todos, setTodos] = useState([]);
  const [text, setText] = useState("");

  const addTodo = () => {
    todos.push(text);
    setTodos(todos);
    setText("");
  };

  const removeTodo = (index) => {
    todos.splice(index, 1);
    setTodos(todos);
  };

  return (
    <div>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
      />

      <button onClick={addTodo}>Add</button>

      {todos.map((todo, index) => (
        <div key={Math.random()}>
          {todo}
          <button onClick={() => removeTodo(index)}>
            Delete
          </button>
        </div>
      ))}
    </div>
  );
}

export default TodoApp;
