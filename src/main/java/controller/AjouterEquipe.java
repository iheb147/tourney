import React, { useState } from "react";

function TodoApp() {
  const [todos, setTodos] = useState([]);
  const [text, setText] = useState("");

  const addTodo = () => {
    if (text.trim() === "") return;
    setTodos([...todos, text.trim()]);
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
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            addTodo();
          }
        }}
      />

      <button onClick={addTodo}>Add</button>

      {todos.length === 0 ? (
        <p>No todos yet. Add one above!</p>
      ) : (
        todos.map((todo, index) => (
          <div key={index}>
            {todo}
            <button onClick={() => removeTodo(index)}>
              Delete
            </button>
          </div>
        ))
      )}
    </div>
  );
}

export default TodoApp;