# Notes Manager

A JavaFX-based desktop application for managing notes with reminders, priority-based scheduling, and AI-powered features.

## Features

### Core Features
- **Create, Edit, Delete Notes**: Full CRUD operations for managing your notes
- **Reminders**: Add date/time reminders with priority levels (1-5)
- **Priority Queue**: Reminders sorted by time (earliest first), then by priority
- **Overdue Indicators**: Past reminders shown in red with "OVERDUE" label
- **Time Display**: Both relative ("In 2 hours") and absolute ("Jan 20, 2:30 PM") times
- **Data Persistence**: Notes automatically saved to `~/.notesmanager/notes.json`
- **Clean UI**: Modern interface with split-pane layout

### 🤖 AI-Powered Features (New!)
- **Smart Summarization**: Auto-generate concise summaries of long notes using GPT
- **Auto-Tag Generation**: AI analyzes content and suggests relevant tags/categories
- **Task Extraction**: Automatically detect and extract action items from note content
- **Smart Priority Suggestions**: AI suggests appropriate priority levels based on content urgency
- **Tags Display**: Visual tag indicators in note list with blue styling

## Technology Stack

- **Java 17+**
- **JavaFX 21.0.1** for UI
- **Gson 2.10.1** for JSON persistence
- **OpenAI GPT API** for AI features (gpt-3.5-turbo)
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
│   │       ├── ai/
│   │       │   ├── AIService.java          # AI service interface
│   │       │   ├── OpenAIService.java      # OpenAI API implementation
│   │       │   └── AIConfig.java           # AI configuration manager
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

### Basic Operations
1. **Create a Note**: Click "New Note" button, enter title and content, optionally add a reminder
2. **Edit a Note**: Select a note from the list and click "Edit"
3. **Delete a Note**: Select a note and click "Delete"
4. **Add Reminder**: When creating/editing, check "Add Reminder" and set date/time/priority
5. **View Reminders**: Right panel shows upcoming reminders sorted by time and priority

### AI Features Setup

To use AI-powered features, you need an OpenAI API key:

1. **Get an API Key**:
   - Visit [OpenAI Platform](https://platform.openai.com/api-keys)
   - Create an account or sign in
   - Generate a new API key

2. **Configure in App**:
   - Open Notes Manager
   - Click "New Note" or "Edit" on any note
   - Click "Configure AI" button
   - Paste your OpenAI API key
   - Click OK

3. **Use AI Features**:
   - **Summarize**: Click "Summarize" to generate a concise summary of your note
   - **Generate Tags**: Click "Generate Tags" to auto-create relevant tags
   - **Extract Tasks**: Click "Extract Tasks" to find action items in your note
   - Tags will appear in blue below note content in the main list

**Note**: Your API key is stored locally at `~/.notesmanager/ai-config.properties`. AI features require an active internet connection and will incur OpenAI API usage costs.

## Data Storage

Notes are automatically saved to:
- **Windows**: `C:\Users\<username>\.notesmanager\notes.json`
- **macOS/Linux**: `~/.notesmanager/notes.json`

## Implementation Highlights

- **Custom Data Structures**: Hand-built linked list and priority queue implementations
- **Singleton Pattern**: NotesManager service ensures single source of truth
- **Automatic Persistence**: All changes auto-saved immediately
- **AI Integration**: OpenAI GPT-3.5 Turbo for smart summarization, tagging, and task extraction
- **Async Processing**: AI operations run in background threads to keep UI responsive
- **Clean Architecture**: Separation of concerns with models, controllers, services, persistence, and AI layers
- **Secure Configuration**: API keys stored locally and never transmitted except to OpenAI

## License

This project is open source and available under the MIT License.
