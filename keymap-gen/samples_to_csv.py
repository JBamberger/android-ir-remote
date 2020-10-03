import re
import csv
import argparse
import pathlib

def read_records(infile):
    sample_parts = []
    with open(infile, 'r', encoding='utf8') as f:
        parts = None
        for line in f:
            if line.startswith('Sample nr.'):
                if parts is not None:
                    sample_parts.append(parts)
                parts = []
            
            if line and line.strip():
                parts.append(line.strip())
        
        if parts is not None and parts:
            sample_parts.append(parts)
    
    records = []
    # add the header row:
    records.append([
                "sample_nr",
                "protocol",
                "decoded_value",
                "sample_count",
                "gap",
                "head_mark","head_space",
                *([v for i in range(47) for v in [f"{i}m", f"{i}s"]]),
                "tail_mark",
                "extent",
                "mark_min", "mark_max",
                "space_min", "space_max",
            ])
    for sample_lines in sample_parts:
        sample_nr = re.match(r'Sample nr.(\d+)', sample_lines[0]).group(1)
        protocol, decoded_value = re.match(r'Decoded ([^:]+): Value:(\d+) \((\d+) bits\)', sample_lines[1]).group(1,2)
        sample_count, gap = re.match(r'Raw samples\((\d+)\): Gap:(\d+)', sample_lines[2]).group(1,2)
        head_mark, head_space = re.match(r'Head: m(\d+)  s(\d+)', sample_lines[3]).group(1,2)

        sample = [sample_nr, protocol, decoded_value, sample_count, gap, head_mark, head_space]

        values = ' '.join(sample_lines[4:16])
        for match in re.finditer(r'(\d+):m(\d+)\W+s(\d+)', values):
            sample.append(match.group(2)) # mark
            sample.append(match.group(3)) # space

        tail_mark = re.match(r'48:m(\d+)', sample_lines[16]).group(1)
        extent = re.match(r'Extent=(\d+)', sample_lines[17]).group(1)
        mark_min, mark_max = re.match(r'Mark\W+min:(\d+)\W+max:(\d+)', sample_lines[18]).group(1,2)
        space_min, space_max = re.match(r'Space\W+min:(\d+)\W+max:(\d+)', sample_lines[19]).group(1,2)

        sample += [tail_mark, extent, mark_min, mark_max, space_min, space_max]
        records.append(sample)

    return records


def write_csv(outfile, records):
    with open(outfile, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        for record in records:
            writer.writerow(record)


def main():
    parser = argparse.ArgumentParser("Converts ir samples to a csv file.")
    parser.add_argument('-i', '--infile', type=pathlib.Path, help="Input file")
    parser.add_argument('-o', '--outfile', type=pathlib.Path, help="Output file")

    args = parser.parse_args()
    records = read_records(args.infile)
    write_csv(args.outfile, records)


if __name__ == '__main__':
    main()
