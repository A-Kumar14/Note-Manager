# Notes Manager

A JavaFX-based desktop application for managing notes with reminders and priority-based scheduling.

## Features

- **Create, Edit, Delete Notes**: Full CRUD operations for managing your notes
- **Reminders**: Add date/time reminders with priority levels (1-5)
- **Priority Queue**: Reminders sorted by time (earliest first), then by priority
- **Overdue Indicators**: Past reminders shown in red with "OVERDUE" label
- **Time Display**: Both relative ("In 2 hours") and absolute ("Jan 20, 2:30 PM") times
- **Data Persistence**: Notes automatically saved to `~/.notesmanager/notes.json`
- **Clean UI**: Modern interface with split-pane layout

## Technology Stack

- **Java 17+**
- **JavaFX 21.0.1** for UI
- **Gson 2.10.1** for JSON persistence
- **Maven** for build management

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/arssh/notesmanager/
│   │       ├── App.java                    # Main application class
│   │       ├── Note.java                   # Note data model
│   │       ├── Reminder.java               # Reminder data model
│   │       ├── controllers/
│   │       │   ├── MainController.java     # Main window controller
│   │       │   └── NoteEditorController.java # Note editor dialog
│   │       ├── persistence/
│   │       │   ├── PersistenceService.java # Persistence interface
│   │       │   ├── JSONPersistence.java    # JSON file persistence
│   │       │   └── LocalDateTimeAdapter.java # Gson date adapter
│   │       ├── services/
│   │       │   └── NotesManager.java       # Singleton service manager
│   │       ├── structures/
│   │       │   ├── NoteLL.java             # Linked list implementation
│   │       │   ├── NoteNode.java           # Node for linked list
│   │       │   └── ReminderPriorityQueue.java # Priority queue for reminders
│   │       └── util/
│   │           └── TimeFormatter.java      # Time formatting utilities
│   └── resources/
│       └── com/arssh/notesmanager/
│           ├── main.fxml                   # Main window layout
│           ├── noteEditor.fxml             # Note editor dialog layout
│           └── styles.css                  # Application stylesheet
```

## Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher

## Installation & Running

### Using Maven:

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/notes-manager.git
cd notes-manager

# Run the application
mvn clean javafx:run
```

### Using an IDE (IntelliJ IDEA, Eclipse, VS Code):

1. Open the project folder
2. Wait for Maven to download dependencies
3. Run the `App.java` main class

## Usage

1. **Create a Note**: Click "New Note" button, enter title and content, optionally add a reminder
2. **Edit a Note**: Select a note from the list and click "Edit"
3. **Delete a Note**: Select a note and click "Delete"
4. **Add Reminder**: When creating/editing, check "Add Reminder" and set date/time/priority
5. **View Reminders**: Right panel shows upcoming reminders sorted by time and priority

## Data Storage

Notes are automatically saved to:
- **Windows**: `C:\Users\<username>\.notesmanager\notes.json`
- **macOS/Linux**: `~/.notesmanager/notes.json`

## Implementation Highlights

- **Custom Data Structures**: Hand-built linked list and priority queue implementations
- **Singleton Pattern**: NotesManager service ensures single source of truth
- **Automatic Persistence**: All changes auto-saved immediately
- **Clean Architecture**: Separation of concerns with models, controllers, services, and persistence layers

## License

This project is open source and available under the MIT License.
