import { Link } from "react-router-dom";
import { cn } from "@/lib/utils";

type LogoProps = {
  url?: string;
  linked?: boolean;
  className?: string;
};

const Logo = (props: LogoProps) => {
  const { url = "/", linked = true, className } = props;
  const icon = (
    <div
      className={cn(
        "relative flex h-8 w-8 items-center justify-center overflow-hidden rounded-md border border-stone-900/10 bg-primary shadow-sm",
        className
      )}
      aria-hidden="true"
    >
      <span className="absolute left-1.5 top-1.5 h-1 w-5 rounded-full bg-[#f8efe3]" />
      <span className="absolute left-[13px] top-1.5 h-5 w-1 rounded-full bg-[#f8efe3]" />
    </div>
  );

  return (
    <div className="flex items-center justify-center sm:justify-start">
      {linked ? <Link to={url}>{icon}</Link> : icon}
    </div>
  );
};

export default Logo;
