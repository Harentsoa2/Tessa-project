import {
  ArrowRight,
  BarChart3,
  CheckCircle,
  Circle,
  HelpCircle,
  Lock,
  Loader2,
  LogOut,
  ShieldCheck,
  Sparkles,
  Timer,
  Users,
  View,
} from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import Logo from "@/components/logo";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import useAuth from "@/hooks/api/use-auth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { logoutMutationFn } from "@/lib/api";
import { toast } from "@/hooks/use-toast";

const stats = [
  { value: "7", label: "work modes" },
  { value: "24h", label: "session continuity" },
  { value: "3x", label: "faster planning rhythm" },
];

const features = [
  {
    icon: Timer,
    title: "Task flow",
    text: "Plan backlog, active work, review and delivery without changing tools.",
  },
  {
    icon: Users,
    title: "Workspace teams",
    text: "Keep members, roles and permissions clear for every workspace.",
  },
  {
    icon: BarChart3,
    title: "Project signals",
    text: "Read workload, progress and recent activity from focused dashboards.",
  },
  {
    icon: Lock,
    title: "Access control",
    text: "Protect settings and destructive actions with permission-aware screens.",
  },
];

const workflow = [
  {
    title: "Backlog",
    icon: HelpCircle,
    items: ["Collect client feedback", "Shape Q2 roadmap"],
  },
  {
    title: "In progress",
    icon: Timer,
    items: ["Design task filters", "Wire backend checks"],
  },
  {
    title: "In review",
    icon: View,
    items: ["Review release notes", "QA workspace invite"],
  },
  {
    title: "Done",
    icon: CheckCircle,
    items: ["Ship auth flow", "Publish sprint board"],
  },
];

const permissions = [
  "Create projects",
  "Edit tasks",
  "Manage members",
  "View analytics",
];

const Landing = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { data: authData } = useAuth();
  const user = authData?.user;

  const workspaceUrl = user?.currentWorkspace?._id
    ? `/workspace/${user.currentWorkspace._id}`
    : "/sign-in";

  const { mutate: logout, isPending: isLoggingOut } = useMutation({
    mutationFn: logoutMutationFn,
    onSuccess: () => {
      queryClient.resetQueries({
        queryKey: ["authUser"],
      });
      navigate("/");
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  return (
    <main className="min-h-screen overflow-hidden bg-[#f7f0e6] text-stone-950">
      <section className="relative isolate flex min-h-[88svh] flex-col border-b border-stone-900/10 bg-[#ede1d1]">
        <div className="absolute inset-0 -z-10">
          <div className="absolute inset-0 bg-[linear-gradient(90deg,rgba(120,72,38,0.08)_1px,transparent_1px),linear-gradient(180deg,rgba(120,72,38,0.08)_1px,transparent_1px)] bg-[size:48px_48px]" />
          <div className="absolute right-0 top-16 hidden w-[58vw] max-w-5xl rounded-l-lg border-y border-l border-stone-900/10 bg-[#fffaf4]/90 p-4 shadow-2xl shadow-stone-900/10 lg:block">
            <div className="flex items-center justify-between border-b border-stone-900/10 pb-3">
              <div className="flex items-center gap-2">
                <span className="h-3 w-3 rounded-full bg-[#b86b3f]" />
                <span className="h-3 w-3 rounded-full bg-[#dfb35f]" />
                <span className="h-3 w-3 rounded-full bg-[#1e9b8d]" />
              </div>
              <span className="text-xs font-medium uppercase tracking-[0.2em] text-stone-500">
                Live workspace
              </span>
            </div>
            <div className="grid gap-4 pt-4 xl:grid-cols-[1fr_280px]">
              <div className="grid grid-cols-4 gap-3">
                {workflow.map((column) => {
                  const Icon = column.icon;
                  return (
                    <div
                      key={column.title}
                      className="min-h-[320px] rounded-md border border-stone-900/10 bg-[#f7f0e6] p-3"
                    >
                      <div className="mb-4 flex items-center gap-2 text-sm font-semibold">
                        <Icon className="h-4 w-4 text-[#6f3f22]" />
                        {column.title}
                      </div>
                      <div className="space-y-3">
                        {column.items.map((item, index) => (
                          <div
                            key={item}
                            className="rounded-md border border-stone-900/10 bg-white p-3 shadow-sm"
                          >
                            <div className="mb-3 h-2 w-16 rounded-full bg-[#dfb35f]" />
                            <p className="text-sm font-medium leading-5">
                              {item}
                            </p>
                            <div className="mt-4 flex items-center justify-between text-xs text-stone-500">
                              <span>TES-{index + 18}</span>
                              <span>{index + 2}d</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  );
                })}
              </div>
              <div className="space-y-4">
                <div className="rounded-md border border-stone-900/10 bg-[#6f3f22] p-4 text-white">
                  <p className="text-sm text-[#f8efe3]/80">Sprint health</p>
                  <p className="mt-2 text-4xl font-semibold">82%</p>
                  <div className="mt-5 h-2 overflow-hidden rounded-full bg-white/20">
                    <div className="h-full w-[82%] rounded-full bg-[#1e9b8d]" />
                  </div>
                </div>
                <div className="rounded-md border border-stone-900/10 bg-white p-4">
                  <p className="text-sm font-semibold">Member focus</p>
                  <div className="mt-4 space-y-3">
                    {["Design", "Backend", "QA"].map((item, index) => (
                      <div key={item} className="flex items-center gap-3">
                        <span className="flex h-8 w-8 items-center justify-center rounded-md bg-[#ede1d1] text-xs font-bold text-[#6f3f22]">
                          {item.slice(0, 1)}
                        </span>
                        <div className="min-w-0 flex-1">
                          <p className="text-sm font-medium">{item}</p>
                          <div className="mt-1 h-1.5 rounded-full bg-stone-100">
                            <div
                              className="h-full rounded-full bg-[#1e9b8d]"
                              style={{ width: `${82 - index * 18}%` }}
                            />
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <header className="mx-auto flex w-full max-w-7xl items-center justify-between px-5 py-5 lg:px-8">
          <Link to="/" className="flex items-center gap-3 font-semibold">
            <Logo linked={false} />
            <span className="text-xl">Tessa</span>
          </Link>
          <nav className="hidden items-center gap-7 text-sm font-medium text-stone-700 md:flex">
            <a href="#features">Features</a>
            <a href="#workflow">Workflow</a>
            <a href="#security">Security</a>
            <a href="#footer">Contact</a>
          </nav>
          {user ? (
            <div className="flex items-center gap-2">
              <Button asChild variant="ghost" className="hidden sm:inline-flex">
                <Link to={workspaceUrl}>Workspace</Link>
              </Button>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    type="button"
                    variant="ghost"
                    className="h-10 w-10 rounded-full p-0"
                  >
                    <Avatar className="h-9 w-9 rounded-full">
                      <AvatarImage src={user.profilePicture || ""} />
                      <AvatarFallback className="rounded-full border border-gray-400">
                        {user?.name?.split(" ")?.[0]?.charAt(0)}
                        {user?.name?.split(" ")?.[1]?.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-52">
                  <DropdownMenuItem
                    disabled={isLoggingOut}
                    onClick={() => {
                      if (isLoggingOut) return;
                      logout();
                    }}
                  >
                    {isLoggingOut ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      <LogOut className="h-4 w-4" />
                    )}
                    Se deconnecter
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Button asChild variant="ghost" className="hidden sm:inline-flex">
                <Link to="/sign-in">Sign in</Link>
              </Button>
              <Button asChild>
                <Link to="/sign-up">
                  Start
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </Button>
            </div>
          )}
        </header>

        <div className="mx-auto grid w-full max-w-7xl flex-1 items-center gap-10 px-5 pb-16 pt-10 lg:grid-cols-[minmax(0,640px)_1fr] lg:px-8">
          <div className="max-w-3xl">
            <div className="mb-5 inline-flex items-center gap-2 rounded-full border border-stone-900/10 bg-white/70 px-3 py-1 text-sm font-medium text-[#6f3f22]">
              <Sparkles className="h-4 w-4 text-[#1e9b8d]" />
              A calmer command center for focused teams
            </div>
            <h1 className="max-w-4xl text-5xl font-semibold leading-[1.02] tracking-normal text-stone-950 sm:text-6xl lg:text-7xl">
              Tessa keeps project work clear from kickoff to delivery.
            </h1>
            <p className="mt-6 max-w-2xl text-lg leading-8 text-stone-700">
              Bring tasks, projects, members and workspace decisions into one
              warm, readable interface that still connects to your existing
              backend.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Button asChild size="lg" className="h-12 px-6">
                <Link to="/sign-up">
                  Create workspace
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </Button>
              <Button
                asChild
                variant="outline"
                size="lg"
                className="h-12 border-stone-300 bg-white/70 px-6"
              >
                <Link to="/sign-in">Open existing account</Link>
              </Button>
            </div>
          </div>
          <div className="rounded-md border border-stone-900/10 bg-white/75 p-4 shadow-xl shadow-stone-900/10 lg:hidden">
            <MobileBoardPreview />
          </div>
        </div>
      </section>

      <section className="border-b border-stone-900/10 bg-[#fffaf4] px-5 py-10 lg:px-8">
        <div className="mx-auto grid max-w-7xl gap-6 md:grid-cols-3">
          {stats.map((item) => (
            <div key={item.label} className="flex items-end gap-4">
              <span className="text-5xl font-semibold text-[#6f3f22]">
                {item.value}
              </span>
              <span className="pb-2 text-sm font-medium uppercase tracking-[0.18em] text-stone-500">
                {item.label}
              </span>
            </div>
          ))}
        </div>
      </section>

      <section id="features" className="bg-[#f7f0e6] px-5 py-20 lg:px-8">
        <div className="mx-auto max-w-7xl">
          <div className="max-w-2xl">
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-[#1e9b8d]">
              What changes
            </p>
            <h2 className="mt-3 text-3xl font-semibold sm:text-5xl">
              A warmer interface for serious project operations.
            </h2>
          </div>
          <div className="mt-10 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {features.map((feature) => {
              const Icon = feature.icon;
              return (
                <article
                  key={feature.title}
                  className="rounded-md border border-stone-900/10 bg-[#fffaf4] p-5 shadow-sm"
                >
                  <div className="flex h-10 w-10 items-center justify-center rounded-md bg-[#6f3f22] text-white">
                    <Icon className="h-5 w-5" />
                  </div>
                  <h3 className="mt-6 text-lg font-semibold">
                    {feature.title}
                  </h3>
                  <p className="mt-3 text-sm leading-6 text-stone-600">
                    {feature.text}
                  </p>
                </article>
              );
            })}
          </div>
        </div>
      </section>

      <section
        id="workflow"
        className="border-y border-stone-900/10 bg-[#23160f] px-5 py-20 text-white lg:px-8"
      >
        <div className="mx-auto grid max-w-7xl gap-10 lg:grid-cols-[360px_1fr]">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-[#dfb35f]">
              Workflow
            </p>
            <h2 className="mt-3 text-3xl font-semibold sm:text-5xl">
              See the whole board without losing the details.
            </h2>
            <p className="mt-5 leading-7 text-[#f8efe3]/75">
              Tessa gives each team a shared place to scan work, spot delays
              and keep reviews moving.
            </p>
          </div>
          <div className="grid gap-3 md:grid-cols-4">
            {workflow.map((column) => {
              const Icon = column.icon;
              return (
                <div
                  key={column.title}
                  className="rounded-md border border-white/10 bg-white/[0.06] p-4"
                >
                  <div className="flex items-center gap-2 text-sm font-semibold">
                    <Icon className="h-4 w-4 text-[#dfb35f]" />
                    {column.title}
                  </div>
                  <div className="mt-4 space-y-3">
                    {column.items.map((item) => (
                      <div
                        key={item}
                        className="rounded-md border border-white/10 bg-[#fffaf4] p-3 text-stone-900"
                      >
                        <p className="text-sm font-medium leading-5">{item}</p>
                        <div className="mt-3 flex items-center gap-2 text-xs text-stone-500">
                          <Circle className="h-3 w-3 fill-[#1e9b8d] text-[#1e9b8d]" />
                          Ready for handoff
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="bg-[#fffaf4] px-5 py-20 lg:px-8">
        <div className="mx-auto grid max-w-7xl items-center gap-12 lg:grid-cols-2">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-[#1e9b8d]">
              Collaboration
            </p>
            <h2 className="mt-3 text-3xl font-semibold sm:text-5xl">
              Members, projects and tasks stay connected.
            </h2>
            <p className="mt-5 leading-7 text-stone-600">
              Workspace members can move from project planning to task updates
              without losing context, while the interface keeps every action
              easy to scan.
            </p>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            {["Product", "Engineering", "Design", "Operations"].map(
              (team, index) => (
                <div
                  key={team}
                  className="rounded-md border border-stone-900/10 bg-[#f7f0e6] p-5"
                >
                  <div className="flex items-center justify-between">
                    <span className="font-semibold">{team}</span>
                    <span className="rounded-full bg-white px-2 py-1 text-xs text-stone-500">
                      {index + 3} active
                    </span>
                  </div>
                  <div className="mt-6 flex -space-x-2">
                    {["A", "M", "R"].map((initial) => (
                      <span
                        key={initial}
                        className="flex h-9 w-9 items-center justify-center rounded-full border-2 border-[#f7f0e6] bg-[#6f3f22] text-sm font-semibold text-white"
                      >
                        {initial}
                      </span>
                    ))}
                  </div>
                </div>
              )
            )}
          </div>
        </div>
      </section>

      <section
        id="security"
        className="border-y border-stone-900/10 bg-[#ead9c4] px-5 py-20 lg:px-8"
      >
        <div className="mx-auto grid max-w-7xl gap-10 lg:grid-cols-[1fr_420px]">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-[#6f3f22]">
              Control
            </p>
            <h2 className="mt-3 text-3xl font-semibold sm:text-5xl">
              Permissions stay visible, not mysterious.
            </h2>
            <p className="mt-5 max-w-2xl leading-7 text-stone-700">
              Tessa keeps the existing backend authorization model intact and
              presents it with clearer visual states across the workspace.
            </p>
          </div>
          <div className="rounded-md border border-stone-900/10 bg-[#fffaf4] p-5 shadow-sm">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-md bg-[#1e9b8d] text-white">
                <ShieldCheck className="h-5 w-5" />
              </div>
              <div>
                <p className="font-semibold">Workspace role</p>
                <p className="text-sm text-stone-500">Admin permissions</p>
              </div>
            </div>
            <div className="mt-6 grid gap-3">
              {permissions.map((item) => (
                <div
                  key={item}
                  className="flex items-center justify-between rounded-md border border-stone-900/10 bg-white px-3 py-3"
                >
                  <span className="text-sm font-medium">{item}</span>
                  <CheckCircle className="h-4 w-4 text-[#1e9b8d]" />
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="bg-[#f7f0e6] px-5 py-20 lg:px-8">
        <div className="mx-auto grid max-w-7xl gap-10 lg:grid-cols-3">
          {[
            {
              title: "Plan",
              text: "Turn workspace goals into projects and scoped tasks.",
            },
            {
              title: "Track",
              text: "Filter work by status, priority, assignee and due date.",
            },
            {
              title: "Improve",
              text: "Use analytics to see what needs attention next.",
            },
          ].map((step, index) => (
            <div key={step.title} className="flex gap-5">
              <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-[#6f3f22] font-semibold text-white">
                {index + 1}
              </span>
              <div>
                <h3 className="text-xl font-semibold">{step.title}</h3>
                <p className="mt-2 leading-7 text-stone-600">{step.text}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <footer
        id="footer"
        className="border-t border-stone-900/10 bg-[#23160f] px-5 py-12 text-[#f8efe3] lg:px-8"
      >
        <div className="mx-auto flex max-w-7xl flex-col gap-8 md:flex-row md:items-center md:justify-between">
          <div>
            <Link to="/" className="flex items-center gap-3 font-semibold">
              <Logo linked={false} />
              <span className="text-xl">Tessa</span>
            </Link>
            <p className="mt-4 max-w-md text-sm leading-6 text-[#f8efe3]/70">
              A focused project workspace for teams that want warmth,
              structure and reliable backend-connected workflows.
            </p>
          </div>
          <div className="flex flex-wrap gap-3">
            <Button asChild variant="secondary">
              <Link to="/sign-in">Sign in</Link>
            </Button>
            <Button asChild>
              <Link to="/sign-up">Create account</Link>
            </Button>
          </div>
        </div>
      </footer>
    </main>
  );
};

const MobileBoardPreview = () => (
  <div className="space-y-3">
    {workflow.slice(0, 3).map((column) => {
      const Icon = column.icon;
      return (
        <div
          key={column.title}
          className="rounded-md border border-stone-900/10 bg-[#fffaf4] p-3"
        >
          <div className="flex items-center gap-2 text-sm font-semibold">
            <Icon className="h-4 w-4 text-[#6f3f22]" />
            {column.title}
          </div>
          <p className="mt-3 rounded-md bg-[#f7f0e6] p-3 text-sm">
            {column.items[0]}
          </p>
        </div>
      );
    })}
  </div>
);

export default Landing;
