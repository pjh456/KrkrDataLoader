import sys

class RedirectStdout:
    def __init__(self, output_widget):
        self.output_widget = output_widget

    def __enter__(self):
        self.original_stdout = sys.stdout
        sys.stdout = self.output_widget
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        sys.stdout = self.original_stdout
