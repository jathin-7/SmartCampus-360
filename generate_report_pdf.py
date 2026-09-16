import os
import subprocess
import sys

def main():
    project_dir = os.path.dirname(os.path.abspath(__file__))
    html_path = os.path.join(project_dir, "docs", "report_template.html")
    pdf_path = os.path.join(project_dir, "Project_Report_CSE2006_25BAI10611.pdf")

    browsers = [
        r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
        r"C:\Program Files\Google\Chrome\Application\chrome.exe",
        r"C:\Program Files\Microsoft\Edge\Application\msedge.exe"
    ]

    selected_browser = None
    for b in browsers:
        if os.path.exists(b):
            selected_browser = b
            break

    if not selected_browser:
        print("[ERROR] Neither Microsoft Edge nor Google Chrome was found to render the PDF.")
        sys.exit(1)

    print(f"[INFO] Using browser: {selected_browser}")
    print(f"[INFO] Generating PDF report from: {html_path}")
    print(f"[INFO] Output destination: {pdf_path}")

    cmd = [
        selected_browser,
        "--headless",
        "--disable-gpu",
        "--run-all-compositor-stages-before-draw",
        f"--print-to-pdf={pdf_path}",
        "--no-pdf-header-footer",
        html_path
    ]

    result = subprocess.run(cmd, capture_output=True, text=True)
    if os.path.exists(pdf_path) and os.path.getsize(pdf_path) > 0:
        size_kb = os.path.getsize(pdf_path) / 1024
        print(f"[SUCCESS] PDF Generated successfully: {pdf_path} ({size_kb:.2f} KB)")
    else:
        print("[ERROR] PDF generation failed!")
        print("Stdout:", result.stdout)
        print("Stderr:", result.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
