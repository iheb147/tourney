import React, { useState } from "react";

function TodoApp() {
  const [todos, setTodos] = useState([]);
  const [text, setText] = useState("");

  const addTodo = () => {
    setTodos([...todos, text]);
    setText("");
  };

  const removeTodo = (index) => {
    const newTodos = todos.filter((_, i) => i !== index);
    setTodos(newTodos);
  };

  return (
    <div>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
      />

      <button onClick={addTodo}>Add</button>

      {todos.map((todo, index) => (
        <div key={index}>
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