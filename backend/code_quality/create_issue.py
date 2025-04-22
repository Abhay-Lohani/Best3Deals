import os
import sys
import requests
from csv2md import table
import csv

commit = sys.argv[1]
# GitLab instance URL
GITLAB_URL = "https://git.cs.dal.ca/api/v4"

# Personal Access Token (store securely!)
ACCESS_TOKEN = os.getenv("GITLAB_ACCESS_TOKEN")

ISSUE_LABELS = "CI/CD,Deployment,CodeReport,Sprint 3"

# Project details
PROJECT_ID = "120157"
path_to_smells = "smells/"
smell_files = [f for f in os.listdir(path_to_smells) if os.path.isfile(
    os.path.join(path_to_smells, f)) and str(f).strip().endswith(".csv")]

headers = {
    "Private-Token": ACCESS_TOKEN,
    "Content-Type": "application/json"
}

url = f"{GITLAB_URL}/projects/{PROJECT_ID}/issues"
for sf in smell_files:

    with open(os.path.join(path_to_smells, sf)) as csv_file:
        list_smells = list(csv.reader(csv_file))

    raw_md = table.Table(list_smells).markdown()

    title = str(sf).replace(".csv", "")+" for commit - "+str(commit)
    body = {
        "title": title,
        "description": raw_md,
        "labels": ISSUE_LABELS
    }

    # Make the request
    response = requests.post(url, json=body, headers=headers)

    # Check response
    if response.status_code == 201:
        print(f"Issue created successfully: {response.json().get('web_url')}")
    else:
        print(f"Failed to create issue: {response.status_code}, {response.text}")

