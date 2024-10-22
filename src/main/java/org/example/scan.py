import os

def scan_and_copy_text(output_file):
    source_dir = os.path.dirname(os.path.abspath(__file__))
    with open(output_file, 'w') as output:
        for root, dirs, files in os.walk(source_dir):
            for file in files:
                if file != 'scan.py' and file != 'output.txt':  # Исключаем файлы с именем 'scan.py'
                    file_path = os.path.join(root, file)
                    try:
                        with open(file_path, 'r', encoding='utf-8') as f:
                            content = f.read()
                            output.write(f"\n\n------File: {file_path}------\n\n")
                            output.write(content)
                    except Exception as e:
                        print(f"Error reading file {file_path}: {e}")

output_file = 'output.txt'

scan_and_copy_text(output_file)
print("Text content from all files has been copied to", output_file)
