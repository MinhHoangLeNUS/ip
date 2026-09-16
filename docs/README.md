# Duke User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details

## Viewing statistics: `stats`

Shows how many tasks you have, how many of them are done, and how many there are of each type.
It only reads your list, so nothing is changed or saved.

Format: `stats` (nothing may follow the command word)

Example: `stats`

With five tasks (two todos, two deadlines and one event), two of them done, Aster replies:

```
Here are your task statistics:
Total: 5 tasks
Completed: 2 (40%)
Not completed: 3
Todos: 2, Deadlines: 2, Events: 1
```

The completion percentage is rounded down, so 100% appears only when every task is done.
For example, 199 of 200 tasks done shows `Completed: 199 (99%)`.
