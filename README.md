# 📰 News Metadata Scraper

*A lightweight Java-based web application for extracting and displaying news article metadata in a structured format.*

---

## Introduction
The **News Metadata Scraper** is a web-based tool that allows users to extract and view essential metadata from any news article URL.  
It helps journalists, researchers, and digital forensics professionals quickly identify core information like **title, description, author, publication date**, and other useful details directly from the webpage.  

The project uses **Java** and **JSoup** for backend processing and a responsive **HTML/CSS/JavaScript** frontend for interaction.  

---

## Objective
- To automate the extraction of metadata (title, description, author, date, etc.) from online news articles.  
- To present extracted metadata in a clear, tabular, and visually appealing format.  
- To provide a deployable solution accessible online using free hosting platforms like **Render**.

---

## Tech Stack
| Layer | Technology Used |
|--------|------------------|
| **Frontend** | HTML, CSS, JavaScript |
| **Backend** | Java (JSoup Library) |
| **Styling Framework** | Custom CSS |
| **Hosting** | Render (for backend), GitHub Pages/Netlify (for frontend) |
| **Build Tool** | Docker (for containerized deployment) |

---

## Key Features
1) Extracts metadata such as:  
- **Title** of the news article  
- **Description**  
- **Author name**  
- **Publication date**  
- **Keywords**  
- **Meta tags**  

2) Displays results in a **clean, modern table format** under the “Scrape Metadata” button.  
3) Responsive and visually attractive frontend design.  
4) Works with most online news URLs.
5) Export results as CSV or JSON. 
6) Easy to deploy and host on free platforms.

---

## How to Run Locally

### Step 1: Clone the repository  
```bash
git clone https://github.com/your-username/news-metadata-scraper.git
cd news-metadata-scraper
```
### Step 2: Compile and Run the Java file
```bash
javac -cp ".:jsoup-1.21.2.jar" Main.java
java -cp ".:jsoup-1.21.2.jar" Main
```
---

## Hosting (Free Deployment)
### Backend (Render)
1) Push your project to GitHub.

2) Add a Dockerfile (already included).
3) On Render.com, create a New Web Service → choose Docker → connect repo → deploy.

---

## Future Enhancements
1) Support for image metadata extraction (e.g., watermarking analysis).
2) Integration with AI-based fake news detection APIs.
3) Add browser extension support for quick scraping.

---

## Developer
Developed by: Saurabh Kisan Butale
