# Inverted Index with Hadoop MapReduce

This project implements a simple **Inverted Index** using Hadoop's **MapReduce API** in Java.  
It scans through a collection of text files, extracts words of a given minimum length, and builds an index mapping each word to the files in which it appears.

---

## 📖 What is an Inverted Index?
An **inverted index** is a data structure commonly used in search engines.  
It allows fast full-text searches by mapping **words → documents** rather than scanning all documents every time.

Example:
word1 → file1.txt, file3.txt
word2 → file2.txt, file3.txt

```

---

## ⚙️ Features
- Extracts tokens from multiple text files.
- Converts all words to lowercase.
- Removes punctuation and non-alphabetic characters.
- Filters words shorter than a configurable **minimum length**.
- Stores unique filenames for each word (no duplicates).

---
```
## 🏗️ Project Structure

src/
└── invertedIndex/
├── invertedIndex.java # Main driver, Mapper, Reducer

```

---

## 🚀 How It Works
### Mapper
- Reads each line of a file.
- Tokenizes into words.
- Cleans punctuation.
- Emits `(word, filename)` pairs if the word length ≥ `minLength`.

### Reducer
- Aggregates filenames for each word.
- Removes duplicates with a `HashSet`.
- Outputs `(word, list_of_files)`.

---

## ▶️ Running the Program
### Prerequisites
- Java 8+
- Hadoop installed and configured
- Input text files in HDFS

### Command
```bash
hadoop jar invertedIndex.jar invertedIndex \
    /input/path /output/path minLength

```
/input/path → HDFS directory containing input text files

/output/path → HDFS directory for results (must not exist before running)

minLength → minimum number of characters in words to be considered
```
hadoop jar invertedIndex.jar invertedIndex \
    /user/hadoop/input /user/hadoop/output 4

```

Example Input

file1.txt
```
Hadoop is powerful
MapReduce is efficient

```
file2.txt
```
Hadoop scales well
Efficient systems matter

```
Example Output
```
efficient    file1.txt, file2.txt
hadoop       file1.txt, file2.txt
mapreduce    file1.txt
powerful     file1.txt
scales       file2.txt
systems      file2.txt
well         file2.txt

```
📝 Notes

Make sure /output/path does not exist before running (Hadoop requirement).

You can adjust minLength to ignore small words like a, an, is, of.

Works on any dataset of plain text files.

