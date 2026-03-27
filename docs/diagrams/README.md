# Architecture & Sequence Diagrams (Mermaid)

This folder contains low-level design diagrams for the project in Mermaid format. Files:

- `architecture.mmd` — component / high-level architecture flow showing Gateway, services, repositories, Gatling, and scripts.
- `class-model.mmd` — class diagram showing models / DTOs (Product, Cart, CartItem, etc.).
- `sequence-product-list.mmd` — sequence diagram for listing products via the gateway.
- `sequence-cart-lifecycle.mmd` — sequence diagram for cart lifecycle: create -> add item -> checkout.
- `gatling-flow.mmd` — sequence diagram showing how Gatling simulation drives the gateway and services.
- `startup-flow.mmd` — flowchart showing `start-all.sh`, `stop-all.sh`, and `start-gatling.sh` interactions.

Rendering options

1) VSCode (recommended for quick preview)
- Install the "Markdown Preview Mermaid Support" or "Mermaid Markdown Preview" extension.
- Open any `.mmd` file and use the preview to render the diagram.

2) mermaid-cli (command-line)
- Install Node.js and npm, then install mermaid-cli globally:

```bash
npm install -g @mermaid-js/mermaid-cli
```

- Render a diagram to PNG or SVG:

```bash
mmdc -i docs/diagrams/architecture.mmd -o docs/diagrams/architecture.png
mmdc -i docs/diagrams/class-model.mmd -o docs/diagrams/class-model.svg
```

3) Online editor
- Paste the Mermaid content into https://mermaid.live/ to preview and export images.

Notes & next steps

- The Mermaid files reflect the current implementation in the repository (modules, endpoints, scripts).
- If you want, I can:
  - Render PNG/SVGs for all diagrams and add them to this folder.
  - Add the diagrams into the main `README.md` as embedded images.
  - Generate PlantUML or PNG fallback formats.

If you want me to render the images now, tell me which format you prefer (PNG or SVG) and I'll render them and add them to `docs/diagrams/`.
