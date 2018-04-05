package org.enb.iso;

import org.enb.iso.PJMConnect;
import java.util.Hashtable;
import java.util.Arrays;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.commons.cli.*;

public class SamplePJMConnect
{
    static String subscription_key = "104f7be5ffcb4ac1b9af9231cd3ca697";

    private static CommandLine generateCommandLine(final Options options, final String[] commandLineArguments)
    {
        final CommandLineParser cmdLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try
        {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
        }
        catch (ParseException parseException)
        {
            System.out.println(
                "ERROR: Unable to parse command-line arguments "
                + Arrays.toString(commandLineArguments) + " due to: "
                + parseException);
        }
        return commandLine;
    }

    public static void main(String[] args)
    {
        final Option reportNameOption = Option.builder("r")
            .required(true)
            .hasArg(true)
            .longOpt("REPORT_NAME")
            .desc("The name of the report.")
            .build();

        final Option rowCountOption = Option.builder("c")
            .required(false)
            .longOpt("ROW_COUNT")
            .hasArg(true)
            .desc("count of records to return")
            .build();

        final Option startRowOption = Option.builder("s")
            .required(false)
            .longOpt("START_ROW")
            .hasArg(true)
            .desc("row to start record retrieval")
            .build();

        final Option downloadOption = Option.builder("d")
            .required(false)
            .longOpt("DOWNLOAD")
            .hasArg(false)
            .desc("run download version")
            .build();

        final Options options = new Options();
        options.addOption(reportNameOption);
        options.addOption(rowCountOption);
        options.addOption(startRowOption);
        options.addOption(downloadOption);

        CommandLine cl = generateCommandLine(options, args);
        final String reportName = cl.getOptionValue("REPORT_NAME");
        System.out.println("Sample use of MRM PJMConnect Class!");

        try
        {
            Hashtable<String, String> params = new Hashtable<String, String>();
            if (cl.hasOption("START_ROW")) {
                final String startRow = cl.getOptionValue("START_ROW");
                params.put("startRow", startRow);
            }

            if (cl.hasOption("ROW_COUNT")) {
                final String rowCount = cl.getOptionValue("ROW_COUNT");
                params.put("rowCount", rowCount);
            }

            if (cl.hasOption("DOWNLOAD")) {
                params.put("download", "true");
            }

            // params.put("uri", "<value>");
            // params.put("report_name", "<value>");
            // params.put("subscription_key", "<value>");
            // params.put("download", "<value>");
            // params.put("rowCount", "<value>");
            // params.put("sort", "<value>");
            // params.put("order", "<value>");
            // params.put("startRow", "<value>");
            // params.put("isActiveMetadata", "<value>");
            // params.put("fields", "<value>");
            // params.put("effective_date_ept", "<value>");
            // params.put("terminate_date_ept", "<value>");
            // params.put("datetime_beginning_utc", "<value>");
            // params.put("datetime_beginning_ept", "<value>");
            // params.put("datetime_ending_utc", "<value>");
            // params.put("datetime_ending_ept", "<value>");
            // params.put("tie_line", "<value>");
            // params.put("ancillary_service", "<value>");
            // params.put("unit", "<value>");
            // params.put("row_is_current", "<value>");
            // params.put("version_nbr", "<value>");
            // params.put("pnode_id", "<value>");
            // params.put("voltage", "<value>");
            // params.put("type", "<value>");
            // params.put("zone", "<value>");
            // params.put("agg_pnode_id", "<value>");
            // params.put("agg_pnode_name", "<value>");
            // params.put("bus_pnode_id", "<value>");
            // params.put("bus_pnode_name", "<value>");
            // params.put("market_name", "<value>");
            // params.put("period_type", "<value>");
            // params.put("class_type", "<value>");
            // params.put("trade_type", "<value>");
            // params.put("hedge_type", "<value>");

            HttpEntity entity = new PJMConnect.Builder(reportName, SamplePJMConnect.subscription_key).setParams(params).build();

            if(entity != null)
            {
                System.out.println(JsonWriter.formatJson(EntityUtils.toString(entity)));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
