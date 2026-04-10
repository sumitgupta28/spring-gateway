---
name: create-issue
description: Intelligently creates a GitHub issue based on user descriptions.
disable-model-invocation: true
allowed-tools: [Bash]
---

# Create GitHub Issue Skill
When the user asks to create a GitHub issue or report a bug:

1. **Information Gathering**: Check if the current context has enough detail (title, body, labels). If not, ask the user for missing info.
2. **Drafting**: Use the [GitHub CLI (gh)](https://cli.github.com/) to draft the issue. Always include:
    - A clear, concise title.
    - A description containing "Steps to Reproduce", "Expected Behavior", and "Actual Behavior" for bugs.
    - Any relevant file snippets or error logs found in the current workspace.
3. **Execution**: Run the following command:
   `gh issue create --title "[Title]" --body "[Body]" --label "bug,ai-generated"`
4. **Verification**: Confirm to the user that the issue was created and provide the URL returned by the CLI.